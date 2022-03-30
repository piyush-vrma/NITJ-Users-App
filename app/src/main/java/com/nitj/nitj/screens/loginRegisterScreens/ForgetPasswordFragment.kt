package com.nitj.nitj.screens.loginRegisterScreens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nitj.nitj.R
import com.nitj.nitj.util.ConnectionManager
import java.util.regex.Pattern

class ForgetPasswordFragment : Fragment() {

    private lateinit var forgetPassEmail: TextInputEditText
    private lateinit var forgetPassButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var llContent: RelativeLayout
    private lateinit var email: String
    private lateinit var firebaseAuth: FirebaseAuth
    private val TAG = "ForgetPassword Fragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_forget_password, container, false)

        findViews(view)

        forgetPassButton.setOnClickListener {
            validateData()
        }

        llContent.setOnClickListener {
            forgetPassEmail.clearFocus()
        }

        return view
    }

    private val EMAIL_ADDRESS_PATTERN: Pattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    private fun checkEmail(email: String): Boolean {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches()
    }

    private fun validateData() {
        email = forgetPassEmail.text.toString()

        if (email.isEmpty()) {
            forgetPassEmail.error = "Please enter Email"
            forgetPassEmail.requestFocus()
        } else if (!checkEmail(email)) {
            forgetPassEmail.error = "Please provide valid email"
            forgetPassEmail.requestFocus()
        } else {
            sendPassResetLink()
        }
    }

    private fun sendPassResetLink() {
        progressBar.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(requireContext())) {

            try {
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Password reset link sent:success")
                            Toast.makeText(context, "Password reset link sent Success\nPlease check your Email", Toast.LENGTH_LONG)
                                .show()
                            val action = ForgetPasswordFragmentDirections.actionForgotPassDestToLoginDest()
                            findNavController().navigate(action)
                            progressBar.visibility = View.GONE
                        } else {
                            Log.w(TAG, "Password reset link sent:failure", task.exception)
                            Toast.makeText(
                                requireContext(),
                                "Failed.\n${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener(requireActivity()) {
                        Toast.makeText(
                            requireContext(), "Password reset link sent:failure.\n${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    }
            } catch (e: Exception) {
                // progressDialog.dismiss()
                progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "$e\nPassword reset link sent:failure",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            //  progressDialog.dismiss()
            progressBar.visibility = View.GONE
            val dialog = AlertDialog.Builder(context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                // Do Nothing
                val settingIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                context?.startActivity(settingIntent)
                (context as Activity).finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                // Do Nothing
                ActivityCompat.finishAffinity(context as Activity)
            }
            dialog.create()
            dialog.show()
        }

    }

    private fun findViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        llContent = view.findViewById(R.id.llContent)
        forgetPassEmail = view.findViewById(R.id.forgetPassEmail)
        email = forgetPassEmail.text.toString()
        forgetPassButton = view.findViewById(R.id.forgetPassButton)
        firebaseAuth = FirebaseAuth.getInstance()
    }

}