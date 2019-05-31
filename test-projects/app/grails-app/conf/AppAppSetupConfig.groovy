
//default appsetup for the application.

screens {
    defaultActions {
        open {
            enabled = false
            label = "Open"
            icon = "fa fa-eye-open"
            ngClick = "show()"
            row {
                enabled = false
                ngClick = "showRow()"
            }

            show {

            }
        }
    }

    test {
        delete {
            label = "Delete"
        }
    }
}
