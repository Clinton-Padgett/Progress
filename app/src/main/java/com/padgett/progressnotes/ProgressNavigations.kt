package com.padgett.progressnotes

import androidx.navigation.NavHostController
import com.padgett.progressnotes.ProgressNavArgs.CLIENT_ID_ARG
import com.padgett.progressnotes.ProgressNavArgs.NOTE_ID_ARG

object ProgressNavArgs {
    const val CLIENT_ID_ARG = "client_id_arg"
    const val NOTE_ID_ARG = "note_id_arg"
}

object ProgressRouteName {
    const val SIGN_IN = "sign_in"
    const val QUICK_NOTE = "quick_note"
    const val ADD_NOTE = "add_note"
    const val CLIENTS = "clients"
    const val INVOICES = "invoices"
    const val SETTINGS = "settings"
    const val INVOICE_PREVIEW = "invoice_preview"
    const val CLIENT_ADD = "client_add"
    const val CLIENT_EDIT = "client_edit"
    const val NOTE_EDIT = "note_edit"
    const val NOTE_ADD = "note_add"
}

object ProgressRoute {
    const val SIGN_IN = ProgressRouteName.SIGN_IN
    const val QUICK_NOTE = ProgressRouteName.QUICK_NOTE
    const val ADD_NOTE = ProgressRouteName.ADD_NOTE
    const val CLIENTS = ProgressRouteName.CLIENTS
    const val INVOICES = ProgressRouteName.INVOICES
    const val SETTINGS = ProgressRouteName.SETTINGS
    const val INVOICE_PREVIEW = "${ProgressRouteName.INVOICE_PREVIEW}/{$CLIENT_ID_ARG}"
    const val CLIENT_ADD = ProgressRouteName.CLIENT_ADD
    const val CLIENT_EDIT = "${ProgressRouteName.CLIENT_EDIT}/{$CLIENT_ID_ARG}"
    const val NOTE_ADD = "${ProgressRouteName.NOTE_ADD}/{$CLIENT_ID_ARG}"
    const val NOTE_EDIT = "${ProgressRouteName.NOTE_EDIT}/{$CLIENT_ID_ARG}/{$NOTE_ID_ARG}"
}

class ProgressNavigationActions(private val navController: NavHostController) {
    fun navigateToSignIn() {
        navController.navigate(ProgressRoute.SIGN_IN)
    }

    fun navigateToQuickNote() {
        navController.navigate(ProgressRoute.QUICK_NOTE)
    }

    fun navigateToAddNote() {
        navController.navigate(ProgressRoute.ADD_NOTE)
    }

    fun navigateToClients() {
        navController.navigate(ProgressRoute.CLIENTS)
    }

    fun navigateToInvoices() {
        navController.navigate(ProgressRoute.INVOICES)
    }

    fun navigateToSettings() {
        navController.navigate(ProgressRoute.SETTINGS)
    }

    fun navigateToInvoicePreview(clientId: String) {
        navController.navigate("${ProgressRouteName.INVOICE_PREVIEW}/$clientId")
    }

    fun navigateToAddClient() {
        navController.navigate(ProgressRoute.CLIENT_ADD)
    }

    fun navigateToEditClient(clientId: String) {
        navController.navigate("${ProgressRouteName.CLIENT_EDIT}/$clientId")
    }

    fun navigateToAddNote(clientId: String) {
        navController.navigate("${ProgressRouteName.NOTE_ADD}/$clientId")
    }

    fun navigateToEditNote(clientId: String, noteId: String) {
        navController.navigate("${ProgressRouteName.NOTE_EDIT}/$clientId/$noteId")
    }

    fun navigateToAddNoteRoute() {
        navController.navigate(ProgressRoute.ADD_NOTE)
    }
}