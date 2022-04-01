package com.nitj.nitj.screens.loginRegisterScreens

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nitj.nitj.R
import com.nitj.nitj.screens.MainActivity
import com.nitj.nitj.util.ConnectionManager
import java.util.regex.Pattern

class LoginFragment : Fragment() {

    private lateinit var emailInputField: TextInputEditText
    private lateinit var passInputField: TextInputEditText
    private lateinit var openReg: TextView
    private lateinit var openForgetPass: TextView
    private lateinit var loginButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var llContent: RelativeLayout
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private var sharedPreferences: SharedPreferences? = null
    private val TAG = "Login Fragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        findViews(view)

        loginButton.setOnClickListener {
            validateData()
        }

        llContent.setOnClickListener {
            emailInputField.clearFocus()
            passInputField.clearFocus()
        }

        openReg.setOnClickListener {
            openRegister()
        }

        openForgetPass.setOnClickListener {
            openForgetPassword()
        }
        return view
    }

    private fun openForgetPassword() {
        val action = LoginFragmentDirections.actionLoginDestToForgotPassDest()
        findNavController().navigate(action)
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            openMain()
        }
    }

    private fun openMain() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
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
        email = emailInputField.text.toString()
        pass = passInputField.text.toString()

        if (email.isEmpty()) {
            emailInputField.error = "Please enter Email"
            emailInputField.requestFocus()
        } else if (!checkEmail(email)) {
            emailInputField.error = "Please provide valid email"
            emailInputField.requestFocus()
        } else if (pass.isEmpty() || pass.length < 8 || pass.length > 12) {
            passInputField.error = "Please enter password (8 <= pass.len <= 12)"
            passInputField.requestFocus()
        } else {
            signInUser()
        }
    }

    private fun signInUser() {

        progressBar.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(requireContext())) {

            try {
                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = firebaseAuth.currentUser
                            Toast.makeText(context, "Login Success", Toast.LENGTH_SHORT)
                                .show()
                            savePreference(name = user?.displayName.toString(), email = email)
                            openMain()
                            progressBar.visibility = View.GONE
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                requireContext(),
                                "Authentication failed.\n${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            progressBar.visibility = View.GONE
                        }
                    }.addOnFailureListener(requireActivity()) {
                        Toast.makeText(
                            requireContext(), "Authentication failed.\n${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        progressBar.visibility = View.GONE
                    }
            } catch (e: Exception) {
                // progressDialog.dismiss()
                progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "$e\nAuthentication Failed",
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

    private fun openRegister() {
        val action = LoginFragmentDirections.actionLoginDestToRegisterDest()
        findNavController().navigate(action)
    }

    private fun findViews(view: View) {
        openReg = view.findViewById(R.id.openReg)
        openForgetPass = view.findViewById(R.id.openForgetPass)
        progressBar = view.findViewById(R.id.progressBar)
        llContent = view.findViewById(R.id.llContent)
        emailInputField = view.findViewById(R.id.logEmail)
        email = emailInputField.text.toString()
        passInputField = view.findViewById(R.id.logPass)
        pass = passInputField.text.toString()
        loginButton = view.findViewById(R.id.loginButton)
        firebaseAuth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference

        openReg.paintFlags = openReg.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        openForgetPass.paintFlags = openForgetPass.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun savePreference(
        name: String,
        email: String,
    ) {
        sharedPreferences?.edit()?.putString("name", name)?.apply()
        sharedPreferences?.edit()?.putString("email", email)?.apply()
    }

}