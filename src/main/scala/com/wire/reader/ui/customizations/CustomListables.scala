package com.wire.reader.ui.customizations

import android.graphics.Color
import android.widget.{AdapterView, ImageView, TextView}
import com.squareup.picasso.Picasso
import com.wire.reader.R
import com.wire.reader.entitities.Message
import macroid.FullDsl._
import macroid._
import macroid.contrib.Layouts.VerticalLinearLayout
import macroid.contrib.{ImageTweaks, TextTweaks}
import macroid.viewable.Listable

import scala.language.postfixOps

trait CustomListables extends CustomTweaks {

  def messageListable(implicit ctx: ContextWrapper): Listable[Message, VerticalLinearLayout] = {
    Listable[Message].tr {
      l[VerticalLinearLayout](
        w[IdView] <~ TextTweaks.size(12) <~ TextTweaks.color(Color.parseColor("blue")),
        w[TextView] <~ TextTweaks.size(16),
        w[ImageView] <~ hide,
        w[TimestampView] <~ TextTweaks.size(12) <~ TextTweaks.color(Color.parseColor("green"))
      )
    }(message => Transformer {
      case timeView: TimestampView => timeView <~ timeTweak(message.time)
      case idView: IdView => idView <~ text(message.id)
      case textView: TextView => textView <~ text(message.text)
      case imageView: ImageView => downloadableImage(imageView, message.text)
    })
  }

  def messageFromListable(adapterView: AdapterView[_], index: Int): Message =
    adapterView.getItemAtPosition(index).asInstanceOf[Message]

  private def downloadableImage(imageView: ImageView, link: String)(implicit ctx: ContextWrapper): Ui[ImageView] = {
    if (isLink(link)) {
      makeImageDownloadable(imageView, link)
      imageView <~ ImageTweaks.adjustBounds <~ show
    }
    else imageView <~ hide
  }

  private def makeImageDownloadable(imageView: ImageView, link: String)(implicit ctx: ContextWrapper): Unit =
    Picasso.`with`(ctx.application).load(link)
      .error(R.drawable.error)
      .placeholder(R.drawable.placeholder)
      .into(imageView)

  private def isLink(s: String): Boolean = {
    val pattern = "^https?://\\S+$".r
    pattern.pattern.matcher(s).matches
  }
}