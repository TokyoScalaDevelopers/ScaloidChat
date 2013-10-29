package com.meetup.TokyoScalaDevelopers.ScaloidChat

import org.scaloid.common._
import android.graphics.Color
import android.view.View
import android.widget.ScrollView

class ChatActivity extends SActivity {
  lazy val logVerticalLayout = new SVerticalLayout
  lazy val logScrollView = new SScrollView {
    this += logVerticalLayout
  }
  lazy val textEntry = new SEditText()
  lazy val submitButton = new SButton().text("Send")
  lazy val textEntryArea = new SLinearLayout {
    this += textEntry
      .<<
        .Weight(1)
      .>>

    this += submitButton
      .<<
        .Weight(2)
      .>>
      .onClick({
        val message = textEntry.text.toString
        textEntry.text("")
        sendMessage(message)
      })

  }

  onCreate {
    contentView = new SRelativeLayout {

      this += logScrollView
        .<<(FILL_PARENT, WRAP_CONTENT)
          .above(textEntryArea)
        .>>

      this += textEntryArea.<<.alignParentBottom.>>
    }
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
    val nameView = new STextView().text(name)
    val messageView = new STextView().text(message)

    logVerticalLayout += nameView += messageView
  }
}
