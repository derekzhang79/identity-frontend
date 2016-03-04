package test.util

import java.util.Date
import javax.mail._
import org.slf4j.LoggerFactory
import test.util.Config.{ResetPasswordEmail, GoogleTestUserCredentials}

object Mail {
  def logger = LoggerFactory.getLogger(this.getClass)

  def resetPasswordEmailReceived(resetRequestTime: Date): Boolean = {
    val messages = inbox.getMessages()
    val messagesCount = inbox.getMessageCount

    val resetPasswdEmail = messages.find { receivedMail =>
      (receivedMail.getFrom.head.toString.contains(ResetPasswordEmail.from)
        && messagesCount == 1)
    }

    resetPasswdEmail match {
      case Some(email) => true
      case None => false
    }
  }

  def deleteAllMail() = {
    inbox.setFlags(inbox.getMessages, new Flags(Flags.Flag.DELETED), true)
    inbox.expunge()
  }

  private val inbox: Folder = {
    val props = System.getProperties
    props.setProperty("mail.store.protocol", "imaps")

    val session = Session.getDefaultInstance(System.getProperties, null)

    val store = session.getStore("imaps")
    store.connect(
      "imap.googlemail.com",
      ResetPasswordEmail.to,
      GoogleTestUserCredentials.password)

    val gmailInbox = store.getFolder("INBOX")
    gmailInbox.open(Folder.READ_WRITE)

    gmailInbox
  }
}
