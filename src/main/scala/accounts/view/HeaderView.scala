package accounts.view

import java.time.Month

import accounts.core.view.View
import accounts.record.{AccountType, TransactionCategory, TransactionType}
import accounts.viewmodel.{FiltersViewModel, RecordViewModel}

import scalafx.Includes._
import scalafx.application.Platform
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.control.{TextFormatter, _}
import scalafx.scene.layout.{GridPane, HBox, Priority}
import scalafx.util.StringConverter

object HeaderView {
  val NumericMonthRegex = "([0-9]{1,2})".r
}

class HeaderView(filters: FiltersViewModel, addRecord: AddRecordView) extends View {

  val textFilterField = new TextField {
    promptText = "Code"
    hgrow = Priority.Always
    textFormatter = new TextFormatter(StringConverter[Option[Int]](
      Option(_).filter(!_.isEmpty).map(_.toInt),
      _.map(_.toString).getOrElse("")
    )) {
      value <==> filters.textFilter
    }
  }

  val content = new GridPane {
    padding = Insets(top = 5, bottom = 5, left = 15, right = 15)
    hgap = 5
    vgap = 5

    add(new Label {
      text = "Filters"
      style = "-fx-font-size: 16pt"
    }, columnIndex = 0, rowIndex = 0, colspan = 2, rowspan = 1)

    add(new Label {
      text = "Account:"
      padding = Insets(top = 0, bottom = 0, left = 0, right = 5)
    }, columnIndex = 0, rowIndex = 1)

    add(new HBox {
      spacing = 5
      alignment = Pos.CenterLeft
      children = Seq(
        new ComboBox[Option[AccountType]](filters.accountTypeFilters) {
          converter = StringConverter.toStringConverter {
            case None => "All Accounts"
            case Some(at) => at.toString
          }
          value <==> filters.accountTypeFilter
        },
        new HBox {
          alignment = Pos.CenterRight
          hgrow = Priority.Always
          children = Seq(
            new CheckBox {
              text = "Include one-off items"
              padding = Insets(top = 0, bottom = 0, left = 0, right = 10)
              selected <==> filters.includeOneOffs
            }
          )
        }
      )
    }, columnIndex = 1, rowIndex = 1)

    add(new Label {
      text = "Transaction type:"
      padding = Insets(top = 0, bottom = 0, left = 0, right = 5)
    }, columnIndex = 0, rowIndex = 2)

    add(new HBox {
      spacing = 5
      children = Seq(
        new ComboBox[Option[TransactionCategory]](filters.transactionCategoryFilters) {
          converter = StringConverter.toStringConverter {
            case None => "All Categories"
            case Some(tc) => tc.displayString
          }
          value <==> filters.transactionCategoryFilter
        },
        new ComboBox[Option[TransactionType]](filters.transactionTypeFilters) {
          converter = StringConverter.toStringConverter {
            case None => "All Types"
            case Some(tt) => tt.displayString
          }
          value <==> filters.transactionTypeFilter
        }
      )
    }, columnIndex = 1, rowIndex = 2)

    add(new Label {
      text = "Date:"
      padding = Insets(top = 0, bottom = 0, left = 0, right = 5)
    }, columnIndex = 0, rowIndex = 3)

    add(new HBox {
      spacing = 5
      children = Seq(
        new DatePicker {
          promptText = "Start Date"
          converter = RecordViewModel.dateConverter
          // Workaround for https://bugs.openjdk.java.net/browse/JDK-8129400
          focused.onChange { (_, _, focusGained) =>
            if (focusGained) {
              Platform.runLater {
                editor().selectAll()
              }
            }
          }
          value <==> filters.startDateFilter
        },
        new DatePicker {
          promptText = "End Date"
          converter = RecordViewModel.dateConverter
          // Workaround for https://bugs.openjdk.java.net/browse/JDK-8129400
          focused.onChange { (_, _, focusGained) =>
            if (focusGained) {
              Platform.runLater {
                editor().selectAll()
              }
            }
          }
          value <==> filters.endDateFilter
        }
      )
    }, columnIndex = 1, rowIndex = 3)

    add(new Separator {
      orientation = Orientation.Vertical
      padding = Insets(top = 10, bottom = 10, left = 10, right = 10)
    }, columnIndex = 2, rowIndex = 0, colspan = 1, rowspan = 4)

    add(new Label {
      text = "Quick Filters"
      style = "-fx-font-size: 16pt"
    }, columnIndex = 3, rowIndex = 0, colspan = 2, rowspan = 1)

    add(new Label {
      text = "Transaction type:"
      padding = Insets(top = 0, bottom = 0, left = 0, right = 5)
    }, columnIndex = 3, rowIndex = 2)

    add(new HBox {
      spacing = 5
      children = Seq(textFilterField)
    }, columnIndex = 4, rowIndex = 2)

    add(new Label {
      text = "Date:"
      padding = Insets(top = 0, bottom = 0, left = 0, right = 5)
    }, columnIndex = 3, rowIndex = 3)

    add(new HBox {
      spacing = 5
      children = Seq(
        new ComboBox[Option[Month]](filters.monthFilters) {
          editable = true
          promptText = "Month"
          prefWidth = 100
          converter = StringConverter[Option[Month]]({
            Option(_).filter(s => !s.isEmpty && !s.equalsIgnoreCase("all")).map {
              case HeaderView.NumericMonthRegex(s) => Month.of(s.toInt)
              case s => Month.valueOf(s.toUpperCase)
            }
          },
          {
            case None => ""
            case Some(m) => m.toString.toLowerCase.capitalize
          })
          // Workaround for https://bugs.openjdk.java.net/browse/JDK-8129400
          focused.onChange { (_, _, focusGained) =>
            if (focusGained) {
              Platform.runLater {
                editor().selectAll()
              }
            }
          }
          value <==> filters.monthFilter
        },
        new TextField {
          promptText = "Year"
          prefWidth = 50
          textFormatter = new TextFormatter(StringConverter[Option[Int]](
            Option(_).filter(!_.isEmpty).map { s =>
              val i = s.toInt
              if (i > 100) i else i + 2000
            },
            _.map(_.toString).getOrElse("")
          )) {
            value <==> filters.yearFilter
          }
        }
      )
    }, columnIndex = 4, rowIndex = 3)

    add(new Separator {
      orientation = Orientation.Vertical
      padding = Insets(top = 10, bottom = 10, left = 10, right = 10)
    }, columnIndex = 5, rowIndex = 0, colspan = 1, rowspan = 4)

    add(new Label {
      text = "Actions"
      style = "-fx-font-size: 16pt"
    }, columnIndex = 6, rowIndex = 0, colspan = 2, rowspan = 1)

    add(addRecord.button, columnIndex = 6, rowIndex = 1)

  }
}