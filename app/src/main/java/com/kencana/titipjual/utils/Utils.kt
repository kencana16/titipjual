package com.kencana.titipjual.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.kencana.titipjual.MainActivity
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.ui.auth.LoginFragment
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

const val URL_SERVER = "http://192.168.205.143/"
const val BASE_IMG_URL = "${URL_SERVER}pao/assets/images/"
const val BASE_URL = "${URL_SERVER}pao/API/"
val decimalFormat = DecimalFormat("#,##0")

fun <A : Activity> Activity.startNewActivity(activity: Class<A>) {
    Intent(this, activity).also {
        it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(it)
    }
}

fun View.snackbar(message: String, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    action?.let {
        snackbar.setAction("retry") {
            it()
        }
    }
    snackbar.show()
}

fun Fragment.logout() = lifecycleScope.launch {
    if (activity is MainActivity) {
        (activity as MainActivity).performLogout()
    }
}

fun Fragment.handleApiError(
    failure: Resource.Failure,
    retry: (() -> Unit)? = null
) {
    when {
        failure.isNetworkError -> requireView().snackbar(
            "Connection Error",
            retry
        )
        failure.errorCode == 401 -> {
            if (this is LoginFragment) {
                requireView().snackbar("Incorrect email or password")
            } else {
                logout()
            }
        }
        else -> {
            val error = failure.errorBody?.string().toString()
            requireView().snackbar(error)
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateTime(l: Long): String? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(l)
        sdf.format(netDate)
    } catch (e: Exception) {
        // return e.toString()
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun getDateTimeWithTime(l: Long): String? {
    return try {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val netDate = Date(l)
        sdf.format(netDate)
    } catch (e: Exception) {
        // return e.toString()
        ""
    }
}

@SuppressLint("SimpleDateFormat")
fun defaultDateTime(l: Long): String? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val netDate = Date(l)
        sdf.format(netDate)
    } catch (e: Exception) {
        // return e.toString()
        ""
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

enum class TYPE { add, edit }

fun getIndoDay(day: String?): String {
    when (day) {
        "Sunday" -> return "Minggu"
        "Monday" -> return "Senin"
        "Tuesday" -> return "Selala"
        "Wednesday" -> return "Rabu"
        "Thursday" -> return "Kamis"
        "Friday" -> return "Jumat"
        "Saturday" -> return "Sabtu"
        else -> return ""
    }
}
