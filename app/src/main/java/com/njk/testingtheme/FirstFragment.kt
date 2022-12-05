package com.njk.testingtheme

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.installations.FirebaseInstallations
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.njk.testingtheme.databinding.FragmentFirstBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    lateinit var database: DatabaseReference
    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            send.setOnClickListener {
//                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                database = FirebaseDatabase.getInstance("https://busticketsystem-f2ca3-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users")

                val accNo = (1..100).random()
//                val regTokenFcm = MyFirebaseMessagingService.getToken(requireContext())

                FirebaseInstallations.getInstance().id.addOnCompleteListener(
                    OnCompleteListener { task ->
                        if(!task.isSuccessful) {
                            Log.w(TAG, "Fetching Unique ID failed", task.exception)
                            return@OnCompleteListener
                        }
//                        globalUniqueId = task.result.toString()
                        context?.getSharedPreferences("_", FirebaseMessagingService.MODE_PRIVATE)?.edit()?.putString("id", task.result)?.apply()
                        Log.d("firebase", "new unique Token: ${task.result}")
                    })

                FirebaseMessaging.getInstance().token.addOnCompleteListener(
                    OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new FCM registration token
//                    fcmToken = task.result.toString()
                        context?.getSharedPreferences("_", FirebaseMessagingService.MODE_PRIVATE)?.edit()?.putString("fcm", task.result)?.apply()
                        Log.d("firebase", "new FCM token: ${task.result}")
                })

                val fcmToken = getToken(requireContext(), "id")
                val globalUniqueId = getToken(requireContext(), "fcm")
                Log.d("firebase", "Reg token, unique id: $fcmToken , $globalUniqueId")

                val user = User(
                    rfid.text?.toString()?.toInt() ?: 0,
                    balance.text?.toString()?.toInt() ?: 0,
                    pending.text?.toString()?.toInt() ?: 0,
                    ticketStatus = TicketStatus.VALID,
//                    tokenFcm = regTokenFcm ?: "bad"
                )
                database.child("$accNo").setValue(user).addOnSuccessListener {
                    rfid.text?.clear()
                    balance.text?.clear()
                    pending.text?.clear()

                    Toast.makeText(requireContext(), "Yay done 🔥", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed 😔", Toast.LENGTH_SHORT).show()
                }
                Log.d("firebase", user.ticketStatus.toString())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private suspend fun loadFcmToken(token: String){

    }
    private suspend fun loadGlobalId(id: String){

    }
    companion object {
        lateinit var UID: String
        lateinit var FCM: String
        fun getToken(context: Context, key: String): String? {
            return context.getSharedPreferences("_", FirebaseMessagingService.MODE_PRIVATE)
                .getString(key, "empty")
        }
    }
}