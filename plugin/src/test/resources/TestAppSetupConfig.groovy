

screens {

    defaultActions {
        open {
            enabled = false //generally false as one of the columns is usually configured with a link
            label = "Open"
            //the name of the icon from http://fortawesome.github.io/Font-Awesome currently 3.2.1 but will upgrade when we do bootstrap 3
            icon = "fa fa-eye-open"
            ngClick = "show()" //show() is the default, should default to action name with lower case first letter
            //list.enabled = false //these are the defaults false if left out right?
            //show.enabled = false
            row {
                enabled = false
                ngClick = "showRow()"
            }
        }
        update {
            enabled = true
            label = "Update" //default is "edit", null for just the icon
            icon = "fa fa-edit"
            ngClick = "update()" //update() is the default, should default to action name with lower case first letter
            show {
                //enabled = true // not needed as update.enabled = true above
                type = "button" //options are "button","actions-drop-down"
                //ng-click="update()" //??? do we need to overide this or can we keep it standard
            }
            list { //
                enabled = false //to override the default
                multiRow = true // actions that uses selected multiply selected rows, should be on the left from the divider on toolbar
                type = "button" //default is button. options are "button","actions-drop-down"
                label = null //overrides the label above, null for just the icon
                tooltip = "Mass Update"
            }
            row {
                //enabled = true //true by default since it inherits from update.enabled = true
                ngClick = "updateRow()" //not used - as of now we dont have support for generating row action popup from Config.
            }
        }
        delete {
            enabled = true
            label = "Delete"
            tooltip = "Delete"
            show {
                //enabled = true // not needed as enabled is true above, only needs to set to false
                type = "actions-drop-down" //default is button. options are "button","actions-drop-down"
                //ng-click="update()" //??? do we need to overide this or can we keep it standard
            }
            list.enabled = false
            row {
                enabled = true //true by default
                //ngClick="updateRow()" //update() is the default, here for an example
            }

            /*
             * Optional parameter. Defines the order of buttons on a toolbar.
             * The smallest number corresponds to the left position on a toolbar.
             * All buttons without/null parameter will be placed after buttons with specified order.
             */
            order = 100
        }

        excel {
            enabled = false
            label = null
            tooltip = "Export to Excel"
            icon = "fa fa-table" //the name of the icon from font awesome
            list {
                multiRow = true // actions that uses selected multiply selected rows, should be on the left from the divider on toolbar
                enabled = true
                type = "button" //default is button. options are "button","drop-down"
                directives = 'ag-grid-xls-export'
            }

            /*
             * Optional parameter. Defines the order of buttons on a toolbar.
             * The smallest number corresponds to the left position on a toolbar.
             * All buttons without/null parameter will be placed after buttons with specified order.
             */
            order = 1
        }

        create {
            enabled = false
            icon = "fa fa-plus"
            ngClick = "createRecord()"
            tooltip = "Create"
            list {
                enabled = true
                type = "button"
            }
        }
    }

    arTran {
        //these will appear in order on actions list and buttons, ??? does configObject keep order?
        menus {
            update {
                ngClick="massUpdate()"
                fields = ["reasonCodes", "status"] //what fields can be updated
            }
            ptp { //only show on IN/DD/DM
                doctypes = ["IN", "DD", "DM"]
                enabled = true //true default/ can be false in client/role
                label = "PTP" //default to header with capital letter
                tooltip = "Promise To Pay"
                icon = "icon-heart" //the name of the icon from font awesome
                show {
                    type = "button" //default is button. options are "button","drop-down"
                    label = null //overrides the label above
                }
                list.type = "button" //default is button. options are "button","drop-down"
            }
            dispute { //only show on IN/DD/DM
                enabled = true
                doctypes = ["IN", "DD", "DM"]
                label = "Dispute" //default to header with capital letter
                tooltip = "Dispute and Reasons"
                icon = "icon-flag" //the name of the icon from font awesome
                show {
                    type = "button" //default is button. options are "button","drop-down"
                    label = null //overrides the label above
                }
                list.type = "actions-drop-down" //default is button. options are "button","drop-down"
                row.enabled = true

            }
            //this should only be enabled for IN doctypes by default
            viewInvoice {
                enabled = true
                ngClick = "viewInvoice(format)" //use format from below
                format = "html" //html or pdf
                label = "Invoice" //default is "Invoice", null for just the icon
                show {
                    type = "button" //default is button. options are "button","drop-down"
                    label = null
                    tooltip = "View Invoice"
                }
                list.type = "drop-down" //default is button. options are "button","drop-down"
                icon = "icon-print" //the name of the icon from font awesome

                script = "viewInvoice" //name of the scripts to use if custom functionality, leave blank for default
            }

            podRequest {
                enabled = true
                show.type = "button"
                list.type = "drop-down" //default is button. options are "button","drop-down"
                label = "POD Request" //default is "edit", null for just the icon
                showLabel = null //override for show, show just the icon
                icon = "icon-flag" //the name of the icon from font awesome
                script = "podRequest" //name of the scripts to use if custom fuctionality, leave blank for default
                ngClick = "podRequest()"
                show {
                    ngShow = "arTran.showPodRequest"
                }

                row {
                    ngShow = "arTran.showPodRequest"
                }
            }
            delete {
                list {
                    enabled = false
                }

                row {}
            }
        }
        list {
            fields = [
                    "*"
                    , "status.name", "status.id",
                    "tranType.name", "stateName",
                    "dispute.*", "dispute.reason.*",
                    "lineSum.*", "ext.*",
                    "stats.bucketLastNight", "stats.bucketDescription",
                    "customer.id", "customer.name", "customer.num",
                    "custAccount.id", "custAccount.name", "custAccount.num",
                    "customer.pastDue", "customer.totalDue"
            ]

            gridz {
                colModel = [
                        [name: "id", label: "ID", hidden: true],
                        [name: "refnum", label: "Ref #", width: 75, align: "right", formatter: "showLink"],
                        [name: "stats.bucketDescription", label: "Bucket", width: 75],
                        [name: "customer.id", label: "Cust Id", hidden: true],
                        [name: "customer.num", label: "Cust Num", width: 80],
                        [name: "customer.name", label: "Cust Name", width: 175, formatter: "showCustomerLink"],
                        [name: "tranDate", label: "Tran Date", width: 80, formatter: "date", align: "center"],
                        [name: "ext.shipDate", label: "Ship Date", width: 80, formatter: "date", align: "center"],
                        [name: "custAccount.num", label: "Account", align: "right", width: 80, formatter: "showCustAccountLink"],
                        [name: "tranType.name", label: "Type", width: 60, align: "center"],
                        [name: "status.name", label: "Status", width: 50, align: "center"],
                        [name: "dispute.reason.name", label: "Reason", width: 75, align: "center"],
                        [name: "origAmount", label: "Orig Amt", width: 75, align: "right", formatter: "currency"],
                        [name: "amount", label: "Open Amt", width: 75, align: "right", formatter: "currency"],
                        [name: "lineSum.taxTotal", label: "Tax Amt", width: 75, align: "right", formatter: "currency"],
                        [name: "lineSum.shipping", label: "Frgt Amt", width: 75, align: "right", formatter: "currency"],
                        [name: "ponum", label: "PO Num", width: 75],
                        [name: "stateName", label: "State", width: 75]
                ]
                actionPopup = true
                multiselect = true
                shrinkToFit = false
                sortname = "id"
                sortorder = "asc"
                rowList = [10, 20, 50, 100, 1000]
            }
        }
        show {
            fields = [
                    "*", "status.name", "status.id",
                    "tranType.name", "stateName",
                    "dispute.*", "dispute.reason.*",
                    "flex.*", "lineSum.*", "ext.*",
                    "customer.id", "customer.name", "customer.num",
                    "customer.id", "customer.location", "customer.keyContact", 
                    "customer.pastDue", "customer.totalDue",
                    "custAccount.*", "billShip.*", "stats.*", "stats.bucketDescription",
                    "origRefnum", "autoCash", "member.branch.*"
            ]
            tabs {
                //what about PA/CM/DM/DD ??
                lines.enabled = true //should be false by default for anything but docType = IN
                activity.enabled = true // true by default
                info.enabled = true //true by default, what about PA/CM/DM/DD ??
            }
        }
        search{

        }
    }

    customer {
        menus {
            newCustomer {
                enabled = false
                label = "New"
                tooltip = "Create new customer"
                icon = "icon-plus"
                list {
                    enabled = true
                    type = "button"
                    ngClick = "newCustomer()"
                }
            }

            newAccount {
                show {
                    enabled = true
                    label = "New account"
                    ngClick = "newAccount()"
                    type = "drop-down"
                }
            }

            newNote {
                show {
                    enabled = true
                    label = "New Note"
                    href = "#/{{customer.id}}?tab=activities"
                    type = "drop-down"
                }

            }
            showStatement {
                show {
                    enabled = true
                    label = "Show statement"
                    href = "#/{{customer.id}}"
                    ngClick = "exportPdf()"
                    type = "drop-down"
                }
            }
        }

        list {
            fields = ["*", "location.*", "keyContact.*", "org.id", "org.calc.*", "info.phone"]

            gridz {
                colModel = [
                        [name: "id", label: "ID", hidden: true],
                        [name: "num", label: "Num", width: 100, formatter: "showLink"],
                        [name: "name", label: "Name", width: 200, formatter: "showLink"],
                        [name: "org.calc.totalDue", label: "Total Due", width: 90, align: "right", formatter: "currency"],
                        [name: "org.calc.pastDue", label: "Past Due", width: 90, align: "right", formatter: "currency"],
                        [name: "org.calc.aging1", label: "New", width: 75, align: "right", formatter: "currency"],
                        [name: "org.calc.aging2", label: "Current", width: 75, align: "right", formatter: "currency"],
                        [name: "org.calc.aging3", label: "30", width: 75, align: "right", formatter: "currency"],
                        [name: "org.calc.aging4", label: "60", width: 75, align: "right", formatter: "currency"],
                        [name: "org.calc.aging5", label: "90+", width: 75, align: "right", formatter: "currency"],
                        [name: "keyContact.name", label: "Key Contact", width: 100],
                        [name: "keyContact.phone", label: "Contact Phone", width: 120],
                        [name: "info.phone", label: "Main Phone", width: 75],
                ]

                actionPopup = false
                multiselect = true
                shrinkToFit = false
                sortname = "id"
                sortorder = "asc"
                rowList = [10, 20, 50, 100, 1000]

            }
        }
        //FIXME both "org.info.*" and "info.*" should not be needed as they point to the same thing
        show {
            fields = [
                    "*", "location.*",
                    "info.*", "keyContact.*",
                    "org.id", "org.calc.*", "creditInfo.*", "org.member.*",
                    "info.phone", "info.fax", "info.website",
                    "setup.*", "acSetup.*",
                    "setup.class1Id", "setup.class2Id", 
                    "setup.class1.*", "setup.class2.*",
                    "related.*",
                    "related.collector.id", "related.collector.name",
                    "org.member.division.id", "org.member.division.name",
                    "scoreCards", "tags",
                    "stats.lastPaTran.id", "stats.lastPaTran.refnum", "stats.lastPaTran.tranDate", "stats.lastPaTran.origAmount",
                    "stats.lastInTran.id", "stats.lastInTran.refnum", "stats.lastInTran.tranDate", "stats.lastInTran.origAmount",
                    "stats.lastShipTran.id", "stats.lastShipTran.refnum", "stats.lastShipTran.tranDate", "stats.lastShipTran.origAmount",
                    "parent.*"
            ]
        }
    }

    custAccount {
        menus {
            newAccount {
                enabled = false
                label = "New"
                tooltip = "Create new account"
                icon = "icon-plus"
                list {
                    enabled = true
                    type = "button"
                    ngClick = "newAccount()"
                }
            }

            newNote {
                show {
                    enabled = true
                    label = "New Note"
                    href = "#/{{custAccount.id}}?tab=activities"
                    type = "drop-down"
                }

            }

        }
        list  {
            fields = [
                    "*", "location.*",
                    "customer.id", "customer.name", "customer.num",
                    "org.id", "org.location.*",
                    "info.phone", "info.fax", "info.website",
                    "keyContact.*", "calc.*",
                    "type.id", "type.name", "type.code", "type.job", "typeId",
                    "lienId", "lien.*",
                    "creditInfo.*",
                    "setup.*", "setup.class1.*", "setup.class2.*",
                    "org.member.branch.name", "related.collector.*",
                    "scoreCards", "tags",
                    "stats.lastPaTran.id", "stats.lastPaTran.refnum", "stats.lastPaTran.tranDate", "stats.lastPaTran.origAmount",
                    "stats.lastInTran.id", "stats.lastInTran.refnum", "stats.lastInTran.tranDate", "stats.lastInTran.origAmount",
                    "stats.lastShipTran.id", "stats.lastShipTran.refnum", "stats.lastShipTran.tranDate", "stats.lastShipTran.origAmount"
            ]

            gridz {

                colModel = [
                        [name: "id", label: "ID", hidden: true],
                        [name: "customer.id", hidden: true],
                        [name: "customer.num", label: "Cust Num", width: 100],
                        [name: "num", label: "Num", width: 100, formatter: "showLink"],
                        [name: "name", label: "Name", width: 200, formatter: "showLink"],
                        [name: "org.member.branch.name", label: "Profit Center"],
                        [name: "lien.jobName", label: "Job Name", width: 100],
                        [name: "calc.totalDue", label: "Total", width: 75, align: "right", formatter: "currency"],
                        [name: "calc.aging1", label: "New", width: 100, align: "right", formatter: "currency"],
                        [name: "calc.aging2", label: "Current", width: 100, align: "right", formatter: "currency"],
                        [name: "calc.aging3", label: "30", width: 75, align: "right", formatter: "currency"],
                        [name: "calc.aging4", label: "60", width: 75, align: "right", formatter: "currency"],
                        [name: "calc.aging5", label: "90+", width: 75, align: "right", formatter: "currency"],
                        [name: "keyContact.name", label: "Key Contact", width: 150],
                        [name: "keyContact.phone", label: "Contact Phone", width: 150],
                        [name: "info.phone", label: "Main Phone", width: 100]
                ]

                actionPopup = false
                multiselect = true
                shrinkToFit = true
                rowList = [10, 20, 50, 100, 1000]
                sortname = "id"
                sortorder = "asc"
            }
        }
        show  {
            fields = [
                    "*", "location.*",
                    "customer.id", "customer.name", "customer.num",
                    "org.id", "org.location.*",
                    "info.phone", "info.fax", "info.website",
                    "keyContact.*", "calc.*",
                    "type.id", "type.name", "type.code", "type.job", "typeId",
                    "lienId", "lien.*",
                    "creditInfo.*",
                    "setup.*", "setup.class1.*", "setup.class2.*",
                    "org.member.branch.name", "related.collector.*",
                    "scoreCards", "tags",
                    "stats.lastPaTran.id", "stats.lastPaTran.refnum", "stats.lastPaTran.tranDate", "stats.lastPaTran.origAmount",
                    "stats.lastInTran.id", "stats.lastInTran.refnum", "stats.lastInTran.tranDate", "stats.lastInTran.origAmount",
                    "stats.lastShipTran.id", "stats.lastShipTran.refnum", "stats.lastShipTran.tranDate", "stats.lastShipTran.origAmount"
            ]
        }
    }

    activity {
        list {
            fields = [
                    "*",
                    "task.*", "task.completedByName",
                    "task.assignedToName",
                    "task.taskType.id", "task.taskType.name",
                    "task.status.id", "task.status.name",
                    "currentAttachments", "createdByName",
                    "org", "org.id", "org.num", "org.name", "org.orgTypeId"
            ]
        }
        show {
            fields = [
                    "*",
                    "task.*", "task.completedByName",
                    "task.assignedToName",
                    "task.taskType.id", "task.taskType.name",
                    "task.status.id", "task.status.name",
                    "currentAttachments", "createdByName",
                    "org", "org.id", "org.num", "org.name", "org.orgTypeId"
            ]
        }
    }


    arAdjustLine {
        list {
            fields = [
                    "*", "arAdjust.*", "arTran.*", "arTran.tranType.*", "arTran.dispute.reason.*",
                    "arAdjust.arTran.refnum", "arAdjust.arTran.docType", "arAdjust.arTran.id",
                    "arAdjust.arTran.tranTypeName","arAdjust.arTran.origAmount",
                    "arAdjust.arTran.tranDate", "origArTran.*", "origArTran.tranType.*", "origArTran.dispute.reason.*",
                    "arTran.custAccount.name", "arTran.custAccount.num"
            ]
        }
        show {
            fields = [
                    "*", "arAdjust.*", "arTran.*", "arTran.tranType.*", "arTran.dispute.reason.*",
                    "arAdjust.arTran.refnum", "arAdjust.arTran.docType", "arAdjust.arTran.id",
                    "arAdjust.arTran.tranTypeName","arAdjust.arTran.origAmount",
                    "arAdjust.arTran.tranDate", "origArTran.*", "origArTran.tranType.*", "origArTran.dispute.reason.*",
                    "arTran.custAccount.name", "arTran.custAccount.num"
            ]
        }
    }

    contact {
        list {
            fields = [
                    "*",
                    "isKey" // dynamic method, added from ArGrailsPlugin
            ]

            gridz {
                colModel = [
                        [name: "id", label: "ID", hidden: true],
                        [name: "name", label: "Name", width: 100],
                        [name: "email", label: "Email", width: 100, formatter: "email"],
                        [name: "phone", label: "Phone", width: 100],
                        [name: "jobTitle", label: "Title", width: 100],
                        [name: "isKey", label: "Key", width: 50, align: "center", formatter: "okIcon"]
                ]

                multiselect = false
                shrinkToFit =  true
                autowidth = true
                sortname = "id"
                sortorder = "asc"

            }
        }

        show {
            fields = [
                    "*",
                    "isKey" // dynamic method, added from ArGrailsPlugin
            ]
        }
    }

    arTranLine {
        list {
            fields = [
                    "*",
                    "flex.text1", "flex.text2", "flex.text3", "flex.text4",
                    "flex.num1"
            ]
        }

        show {
            fields = [
                    "*",
                    "flex.text1", "flex.text2", "flex.text3", "flex.text4",
                    "flex.num1"
            ]
        }
    }

    custAccountLien{
        show {
            fields = [
                    "*",
                    'custAccount.id',
                    'lender.*', 'lender.location.*',
                    'bond.*', 'bond.location.*', 'bond.flex.*',
                    'contractor.*', 'contractor.location.*',
                    'owner.*', 'owner.location.*',
            ]
        }
    }

    disputeReview {
        list {
            fields = [
                    "*", "status.name", "status.id",
                    "tranType.name", "stateName",
                    "dispute.*", "dispute.reason.*",
                    "customer.id", "customer.name", "customer.num",
                    "customer.org.id", "customer.location", "customer.keyContact",
                    "customer.pastDue", "customer.totalDue",
                    "custAccount.*",
                    "org.member.branch.*", "origRefnum","autoCash"
            ]

            gridz {
                colModel = [
                        [ name: "id", label: "ID", hidden: true ],

                        [ name: "refnum", label: "Ref #", width: 75, align: "right", formatter: "showLink" ],
                        [ name: "origRefnum", label: "Orig Ref #", width: 75, align: "right", formatter: "showLink" ],
                        [ name: "tranDate", label: "Tran Date", width: 75, formatter: "date" ],

                        [ name: "org.member.branch.name", label: "PC", width: 75 ],
                        [ name: "customer.id", label: "Cust Id", hidden: true ],
                        [ name: "customer.name", label: "Cust Name", width: 175 ],
                        [ name: "custAccount.num", label: "Account", align: "right", width: 80 ],
                        [ name: "tranType.name", label: "TrnType", width: 50, align: "center" ],
                        [ name: "status.name", label: "Status", width: 50, align: "center" ],
                        [ name: "dispute.reason.name", label: "Reason", width: 75, align: "center" ],
                        [ name: "amount", label: "Open Amt", width: 75, align: "right", formatter: "currency" ],
                ]

                multiselect = true
                shrinkToFit =true
                sortname = "id"
                sortorder = "asc"
            }
        }

        show {
            fields = [
                    "*", "status.name", "status.id",
                    "tranType.name", "stateName",
                    "dispute.*", "dispute.reason.*",
                    "customer.id", "customer.name", "customer.num",
                    "customer.org.id", "customer.location", "customer.keyContact",
                    "customer.pastDue", "customer.totalDue",
                    "custAccount.*",
                    "org.member.branch.*", "origRefnum","autoCash"
            ]
        }
    }

    org {
        list {
            fields = [
                    "id", "num", "name",
                    "rootOrgId", "rootOrg.id", "rootOrg.name",
                    "orgTypeId", "type.name",
                    "info.*", "location.*",
                    "flex.num6"
            ]

            gridz {
                colModel = [
                        [ name: "id", label: "ID", hidden: true ],
                        [ name: "num", label: "Num", width: 100, formatter: "editActionLink" ],
                        [ name: "name", label: "Name", formatter: "editActionLink" ],
                        [ name: "type.name", label: "Type", width: 100, formatter: "editActionLink" ]
                ]

                multiselect = false
                shrinkToFit = true
                actionPopup = false
                sortname = "id"
                sortorder = "asc"
            }
        }

        show {
            fields = [
                    "id", "num", "name",
                    "rootOrgId", "rootOrg.id", "rootOrg.name",
                    "orgTypeId", "type.name",
                    "info.*", "location.*",
                    "flex.num6"
            ]
        }
    }

    task {
        list {
            gridz {
                colModel = [
                        [ name: "customer.id", label: "ID", hidden: true ],
                        [ name: "customer.num", label: "Num", width: 75, formatter: "showCustomerActivitiesLink" ],
                        [ name: "customer.name", label: "Name", width: 200, formatter: "showCustomerActivitiesLink" ],
                        [ name: "task.taskType.name", label: "Task", width: 50 ],
                        [ name: "task.priority", label: "Priority", width: 50, formatter: "priorityLabel" ],
                        [ name: "task.activity.title", label: "Title" ],
                        [ name: "task.createdDate", label: "Created Date", formatter: "date", width: 80 ],
                        [ name: "task.dueDate", label: "Due Date", formatter: "date", width: 80 ]
                ]

                multiselect = true
                shrinkToFit = true
                sortname = "id"
                sortorder = "asc"
            }
        }

        show {
            fields = [ "*"]
        }
    }


}