package com.kencana.titipjual.ui.penjual

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kencana.titipjual.MainActivity
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.databinding.FragmentFormPenjualBinding
import com.kencana.titipjual.ui.produk.FormProductFragmentArgs
import com.kencana.titipjual.utils.TYPE
import com.kencana.titipjual.utils.afterTextChanged
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FormPenjualFragment : Fragment() {

    private val args: FormProductFragmentArgs by navArgs()
    private var _binding: FragmentFormPenjualBinding? = null
    private val binding: FragmentFormPenjualBinding get() = _binding!!
    private val penjualViewModel: PenjualViewModel by hiltNavGraphViewModels(R.id.nav_penjual)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormPenjualBinding.inflate(inflater, container, false)

        if (args.type == TYPE.add) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Add Penjual"
            penjualViewModel.resetSelectedSeller()
        } else if (args.type == TYPE.edit) {
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit Penjual"
            binding.etSellerName.setText(penjualViewModel.selectedSeller.value?.namaPenjual)
            binding.etSellerPhone.setText(penjualViewModel.selectedSeller.value?.noHp)
            binding.etSellerAddress.setText(penjualViewModel.selectedSeller.value?.alamat)
            submitForm()
        }

        setupUI()
        updateUI()

        return binding.root
    }

    private fun setupUI() {
        binding.etSellerName.afterTextChanged {
            submitForm()
        }
        binding.etSellerPhone.afterTextChanged {
            submitForm()
        }
        binding.etSellerAddress.afterTextChanged {
            submitForm()
        }

        binding.btnSimpan.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            penjualViewModel.save()
        }

        binding.tvContactPicker.setOnClickListener {
            pickContactIntent()
        }
    }

    private fun submitForm() {
        penjualViewModel.submitForm(
            binding.etSellerName.text.toString(),
            binding.etSellerPhone.text.toString(),
            binding.etSellerAddress.text.toString()
        )
    }

    private fun updateUI() {

        penjualViewModel.sellerFormState.observe(viewLifecycleOwner) {
            binding.btnSimpan.isEnabled = it.isDataValid

            binding.tietSellerName.error = it.namaError
            binding.tietSellerPhone.error = it.hpError
            binding.tietSellerAddress.error = it.alamatError
        }

        penjualViewModel.sellerList.observe(viewLifecycleOwner) {
            binding.loading.visibility = View.GONE

            if (it is Resource.Success && it.value.message.equals("Data created", true)) {
                Toast.makeText(requireContext(), "Tambah data berhasil", Toast.LENGTH_SHORT).show()

                binding.etSellerName.text = null
                binding.etSellerPhone.text = null
                binding.etSellerAddress.text = null
                penjualViewModel.resetSelectedSeller()
                penjualViewModel.resetMessageResponse()
                findNavController().navigate(FormPenjualFragmentDirections.actionFormPenjualFragmentToPenjualFragment())
            } else if (it is Resource.Success && it.value.message.equals("Data updated", true)) {
                Toast.makeText(requireContext(), "Update data berhasil", Toast.LENGTH_SHORT).show()

                binding.etSellerName.text = null
                binding.etSellerPhone.text = null
                binding.etSellerAddress.text = null

                val oldItem = penjualViewModel.selectedSeller.value
                val newItem = it.value.data!!.penjual!!.first { item ->
                    item.idPenjual == oldItem!!.idPenjual
                }
                penjualViewModel.updateSelectedProduct(newItem)
                penjualViewModel.resetMessageResponse()
                findNavController().navigate(FormPenjualFragmentDirections.actionFormPenjualFragmentToPenjualFragment())
            }
        }
    }

    private fun pickContactIntent() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CONTACT_PICK_CODE) {
                binding.etSellerPhone.text = null

                val uri = data?.data

                val cursor1 =
                    requireActivity().contentResolver.query(uri!!, null, null, null, null)
                var cursor2: Cursor? = null

                if (cursor1!!.moveToFirst()) {
                    val contactId =
                        cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    val idResult =
                        cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    val idResultHold = idResult?.toInt()

                    if (idResultHold == 1) {
                        cursor2 = requireActivity().contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = $contactId",
                            null,
                            null
                        )

                        while (cursor2!!.moveToNext()) {
                            val contactNumber =
                                cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            if (contactNumber.first().equals('0', true)) {
                                binding.etSellerPhone.setText(contactNumber)
                            }  else {
                                binding.etSellerPhone.setText(
                                    "0${
                                        contactNumber.replace("+", "").replace("-", "")
                                            .removePrefix("62")
                                    }"
                                )
                            }
                        }
                        cursor2.close()
                    }
                }
                cursor1?.close()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val CONTACT_PICK_CODE = 1
    }
}
