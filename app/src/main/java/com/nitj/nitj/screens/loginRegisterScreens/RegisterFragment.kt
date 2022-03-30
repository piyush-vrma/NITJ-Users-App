package com.nitj.nitj.screens.loginRegisterScreens

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
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


class RegisterFragment : Fragment() {

    private lateinit var nameInputField: TextInputEditText
    private lateinit var emailInputField: TextInputEditText
    private lateinit var passInputField: TextInputEditText
    private lateinit var openLog: TextView
    private lateinit var registerButton: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var llContent : RelativeLayout
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var pass: String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var reference: DatabaseReference
    private val TAG = "Register Fragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        findViews(view)

        registerButton.setOnClickListener {
            validateData()
        }

        llContent.setOnClickListener {
            nameInputField.clearFocus()
            emailInputField.clearFocus()
            passInputField.clearFocus()
        }

        openLog.setOnClickListener {
            openLogin()
        }

        return view
    }

    private fun openLogin() {
        val action = RegisterFragmentDirections.actionRegisterDestToLoginDest2()
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
        name = nameInputField.text.toString()
        email = emailInputField.text.toString()
        pass = passInputField.text.toString()

        if (name.isEmpty()) {
            nameInputField.error = "Please enter userName"
            nameInputField.requestFocus()
        } else if (email.isEmpty()) {
            emailInputField.error = "Please enter Email"
            emailInputField.requestFocus()
        } else if (!checkEmail(email)) {
            emailInputField.error = "Please provide valid email"
            emailInputField.requestFocus()
        } else if (pass.isEmpty() || pass.length < 8 || pass.length > 12) {
            passInputField.error = "Please enter password (8 <= pass.len <= 12)"
            passInputField.requestFocus()
        } else {
            createUser()
        }
    }

    private fun createUser() {
       progressBar.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(requireContext())) {

            try {
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success")
                            val user = firebaseAuth.currentUser
                            uploadData()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
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
            }catch (e: Exception) {
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

    private fun uploadData() {

        if (ConnectionManager().checkConnectivity(requireContext())) {

            try {
                reference = reference.child("Users")
                val key = reference.push().key.toString()

                val user: HashMap<String, String> = HashMap<String, String>()
                user["key"] = key
                user["name"] = name
                user["email"] = email

                reference.child(key).setValue(user).addOnSuccessListener {
                    Log.e(TAG, "User Upload success")
                    Toast.makeText(context, "Registration Success", Toast.LENGTH_SHORT)
                        .show()
                    openMain()
                    progressBar.visibility = View.GONE
                    // progressDialog.dismiss()

                }.addOnFailureListener {
                    //progressDialog.dismiss()
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "${it.message}}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                // progressDialog.dismiss()
                progressBar.visibility = View.GONE
                Toast.makeText(
                    context,
                    "$e\nRegistration Failed",
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
        nameInputField = view.findViewById(R.id.regName)
        openLog = view.findViewById(R.id.openLog)
        openLog.paintFlags = openLog.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        progressBar = view.findViewById(R.id.progressBar)
        llContent = view.findViewById(R.id.llContent)
        name = nameInputField.text.toString()
        emailInputField = view.findViewById(R.id.regEmail)
        email = emailInputField.text.toString()
        passInputField = view.findViewById(R.id.regPass)
        pass = passInputField.text.toString()
        registerButton = view.findViewById(R.id.registerButton)
        firebaseAuth = FirebaseAuth.getInstance()
        reference = FirebaseDatabase.getInstance().reference
    }

}