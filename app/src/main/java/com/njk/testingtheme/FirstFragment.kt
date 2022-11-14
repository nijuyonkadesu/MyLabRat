package com.njk.testingtheme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.njk.testingtheme.databinding.FragmentFirstBinding

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
                val user = User(
                    rfid.text?.toString()?.toInt() ?: 0,
                    balance.text?.toString()?.toInt() ?: 0,
                    pending.text?.toString()?.toInt() ?: 0,
                    ticketStatus = TicketStatus.VALID
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
}