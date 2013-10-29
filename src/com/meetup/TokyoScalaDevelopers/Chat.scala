package com.meetup.TokyoScalaDevelopers.ScaloidChat

import org.scaloid.common._
import android.graphics.Color
import android.view.View
import android.widget.ScrollView

import de.tavendo.autobahn.WebSocketConnection
import de.tavendo.autobahn.WebSocketException
import de.tavendo.autobahn.WebSocketHandler

class ChatActivity extends SActivity {
  lazy val logVerticalLayout = new SVerticalLayout
  lazy val logScrollView = new SScrollView += logVerticalLayout
  lazy val textEntry = new SEditText()
  lazy val submitButton = new SButton().text("Send")
  lazy val textEntryArea = new SLinearLayout {
    this += textEntry.<<.Weight(1).>>

    this += submitButton
      .<<.Weight(2).>>
      .onClick({
        val message = textEntry.text.toString
        textEntry.text("")
        sendMessage(message)
      })
  }

  lazy val ws = new WebSocketConnection

  onCreate {
    contentView = new SRelativeLayout {

      this += logScrollView
        .<<(FILL_PARENT, WRAP_CONTENT)
          .above(textEntryArea)
        .>>

      this += textEntryArea.<<.alignParentBottom.>>
    }
  }

  onStart {
    ws.connect("ws://echo.websocket.org", new WebSocketHandler {
      override def onOpen {
        receivedMessage("DEBUG", "Connected to server")
      }

      override def onTextMessage(data: String) {
        receivedMessage("DEBUG", data)
      }

      override def onClose(code: Int, reason: String) {
        receivedMessage("DEBUG", s"Disconnected: $reason ($code)")
      }
    })
  }

  def sendMessage(message: String) {
    val nameView = new STextView().text("Me")
    val messageView = new STextView().text(message)

    logVerticalLayout += new SLinearLayout {
      this += messageView.<<.Weight(1).>>
      this += nameView.<<.Weight(3).>>
    }
    logScrollView.fullScroll(View.FOCUS_DOWN)
  }

  def receivedMessage(name: String, message: String) {
    val nameView = new STextView().text(s"$name: ")
    val messageView = new STextView().text(message)

    logVerticalLayout += new SLinearLayout {
      this += nameView
      this += messageView
    }
  }
}
