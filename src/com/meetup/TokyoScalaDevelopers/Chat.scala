package com.meetup.TokyoScalaDevelopers.ScaloidChat

import org.scaloid.common._
import android.graphics.Color
import android.view.View
import android.widget.ScrollView

import de.tavendo.autobahn.WebSocketConnection
import de.tavendo.autobahn.WebSocketException
import de.tavendo.autobahn.WebSocketHandler

import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.JsonNode

case class Packet(kind: String, user: String, message: String, members: List[String])

object JsonHandler {
  import scala.collection.JavaConversions._

  lazy val mapper = new ObjectMapper()

  def getStringOption(node: JsonNode, key: String) = Option(node.get(key)).map(_.asText)
  def getStringListOption(node: JsonNode, key: String) = Option(node.get(key)).map(_.toList.map(_.asText))

  def parsePacket(data: String): Option[Packet] = {
    Option(mapper.readTree(data)).map({ node =>
      for(
        kind <- getStringOption(node, "kind");
        user <- getStringOption(node, "user");
        message <- getStringOption(node, "message");
        members <- getStringListOption(node, "members")
      ) yield Packet(kind, user, message, members)
    }).flatten
  }

  def stringToJson(message: String): String = {
    val node = mapper.createObjectNode()
    node.put("text", message)
    node.toString
  }
}

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
    ws.connect("ws://172.16.255.108:9000/room/chat?username=Devon", new WebSocketHandler {
      override def onOpen {
        receivedMessage("DEBUG", "Connected to server")
      }

      override def onTextMessage(data: String) {
        val packet = JsonHandler.parsePacket(data)
        packet.map(_ match {
          case Packet("talk", user, message, _) => receivedMessage(user, message)
          case Packet("join", user, _, _) => receivedMessage("JOINED", user)
          case Packet("quit", user, _, _) => receivedMessage("QUIT", user)
          case m => receivedMessage("DEBUG", s"$m")
        })
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

    ws.sendTextMessage(JsonHandler.stringToJson(message))
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
