package com.kencana.titipjual.ui.pesanan.add

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.kencana.titipjual.R
import com.kencana.titipjual.data.network.Resource
import com.kencana.titipjual.data.response.DetailBarangItemPesanan
import com.kencana.titipjual.data.response.PenjualItem
import com.kencana.titipjual.databinding.FragmentAddPesananBinding
import com.kencana.titipjual.ui.penjualan.home.adapter.SellerAdapter
import com.kencana.titipjual.ui.pesanan.home.PesananViewModel
import com.kencana.titipjual.utils.afterTextChanged
import com.kencana.titipjual.utils.getDateTimeWithTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPesananFragment : Fragment() {

    private lateinit var sellerAdapter: SellerAdapter
    private val pesananViewModel: PesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private val addPesananViewModel: AddPesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private var _binding: FragmentAddPesananBinding? = null
    private val binding: FragmentAddPesananBinding
        get() = _binding!!
    private lateinit var selectedAdapter: SelectedProductAdapter
    private lateinit var sellerSheet: BottomSheetBehavior<LinearLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddPesananBinding.inflate(inflater, container, false)

        setupUI()
        updateUI()

        return binding.root
    }

    private fun setupUI() {

        binding.tietCustomerName.setText(addPesananViewModel.pesananItem.value?.namaPemesan)
        binding.tietCustomerPhone.setText(addPesananViewModel.pesananItem.value?.noHpPemesan)
        binding.tietCustomerAddress.setText(addPesananViewModel.pesananItem.value?.alamat)

        val items = listOf("Harga normal", "Harga reseller")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.tvPriceType as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.tvPriceType.setText(binding.tvPriceType.adapter.getItem(0).toString(), false)
        binding.tvPriceType.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> addPesananViewModel.setHarga(AddPesananViewModel.Harga.harga_satuan_normal)
                1 -> addPesananViewModel.setHarga(AddPesananViewModel.Harga.harga_satuan_reseller)
                else -> addPesananViewModel.setHarga(AddPesananViewModel.Harga.harga_satuan_normal)
            }
            submitForm()
        }

        binding.tvDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                datePicker()
            }
        }

        binding.tietCustomerName.afterTextChanged {
            submitForm()
        }
        binding.tietCustomerPhone.afterTextChanged {
            submitForm()
        }
        binding.tietCustomerAddress.afterTextChanged {
            submitForm()
        }

        binding.tvSellerPicker.setOnClickListener {
            addPesananViewModel.getSeller()
            sellerSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.tvContactPicker.setOnClickListener {
            pickContactIntent()
        }

        binding.btnAddBarang.setOnClickListener {
            findNavController().navigate(AddPesananFragmentDirections.actionAddPesananFragmentToSelectBarangPesananFragment())
        }

        binding.btnSimpan.setOnClickListener {
            addPesananViewModel.save()
            showLoading()
        }

        binding.recyclerviewDetailBarang.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            selectedAdapter = SelectedProductAdapter()
            this.adapter = selectedAdapter
            selectedAdapter.setOnItemClickListener(object :
                    SelectedProductAdapter.OnItemClickCallback {
                    override fun onDelete(item: DetailBarangItemPesanan) {
                        Toast.makeText(
                            requireContext(),
                            "${item.namaBarang} dihapus",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        addPesananViewModel.removeProduct(item)
                    }
                })
        }

        sellerSheet = BottomSheetBehavior.from(binding.bottomSheetSeller)
        sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
        binding.rvBottomSheetSeller.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            sellerAdapter = SellerAdapter()
            this.adapter = sellerAdapter
            sellerAdapter.setOnItemClickListener(object : SellerAdapter.OnItemClickCallback {
                override fun onClick(item: PenjualItem) {
                    addPesananViewModel.setSeller(item)
                    sellerSheet.state = BottomSheetBehavior.STATE_HIDDEN
                }
            })
        }
    }

    private fun submitForm() {
        addPesananViewModel.submitForm(
            binding.tietCustomerName.text.toString(),
            binding.tietCustomerPhone.text.toString(),
            binding.tietCustomerAddress.text.toString(),
        )
        Log.d(
            TAG,
            "submitForm: \n " +
                "${binding.tietCustomerName.text} - ${binding.tietCustomerPhone.text} - ${binding.tietCustomerAddress.text}"
        )
    }

    private fun updateUI() {
        addPesananViewModel.pesananItem.observe(viewLifecycleOwner) {
            Log.d(TAG, "updateUI: $it")
            if (!it.tglDiambil.isNullOrEmpty()) {
                val date = getDateTimeWithTime(it.tglDiambil.toLong())
                binding.tvDate.setText(date)
            } else {
                binding.tvDate.text = null
            }

            selectedAdapter.submitList(it.detailBarang)
        }

        addPesananViewModel.pesananFormState.observe(viewLifecycleOwner) {
            binding.btnSimpan.isEnabled = it.isDataValid

            binding.tilDate.error = it.tglError
            binding.tilCustomerName.error = it.namaError
            binding.tilCustomerPhone.error = it.hpError
            binding.tilCustomerAddress.error = it.alamatEror
        }

        addPesananViewModel.response.observe(viewLifecycleOwner) {
            stopLoading()
            Log.d(TAG, "updateUI: $it")
            if (it is Resource.Success && it.value.message.equals("Data created")) {
                pesananViewModel.getData()
                addPesananViewModel.reset()

                binding.tietCustomerName.text = null
                binding.tietCustomerPhone.text = null
                binding.tietCustomerAddress.text = null

                AlertDialog.Builder(requireContext())
                    .setMessage("Penjualan dibuat")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }

        addPesananViewModel.sellerList.observe(
            viewLifecycleOwner,
            {
                sellerAdapter.submitList(it)
            }
        )

        addPesananViewModel.seller.observe(
            viewLifecycleOwner,
            {
                addPesananViewModel.pesananItem.value
                binding.tietCustomerName.setText(it.namaPenjual)
                binding.tietCustomerPhone.setText(it.noHp)
                binding.tietCustomerAddress.setText(it.alamat)
                binding.tietCustomerAddress.clearFocus()
            }
        )
    }

    private fun datePicker() {
        // Date Picker
        val builder =
            MaterialDatePicker.Builder.datePicker().setTitleText("Pilih tanggal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {

            val time = it - 25200000
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("Select Appointment time")
                .build()
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val totalSecond: Long =
                    time + (hour.toLong() * 3600000) + (minute.toLong() * 60000)
                addPesananViewModel.setDate("$totalSecond")
                submitForm()
            }
            timePicker.addOnCancelListener { binding.tvDate.clearFocus() }
            timePicker.addOnDismissListener { binding.tvDate.clearFocus() }
            timePicker.show(parentFragmentManager, timePicker.toString())
        }
        picker.addOnCancelListener { binding.tvDate.clearFocus() }
        picker.addOnDismissListener { binding.tvDate.clearFocus() }
    }

    private fun pickContactIntent() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, CONTACT_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONTACT_PICK_CODE) {
//                binding.tietCustomerPhone.text = null

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
                                binding.tietCustomerPhone.setText(contactNumber)
                            } else {
                                binding.tietCustomerPhone.setText(
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
                cursor1.close()
            }
        }
    }

    private fun showLoading() {
        binding.progressCircular.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun stopLoading() {
        binding.progressCircular.visibility = View.GONE
        activity?.window?.clearFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "AddPesananFragment"
        private const val CONTACT_PICK_CODE = 1
    }
}
