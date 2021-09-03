package com.kencana.titipjual.ui.pesanan.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kencana.titipjual.R
import com.kencana.titipjual.data.response.DetailBarangItemPesanan
import com.kencana.titipjual.data.response.PesananItem
import com.kencana.titipjual.databinding.FragmentDetailPesananBinding
import com.kencana.titipjual.ui.pesanan.add.SelectedProductAdapter
import com.kencana.titipjual.ui.pesanan.home.PesananViewModel
import com.kencana.titipjual.utils.decimalFormat
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder

@AndroidEntryPoint
class DetailPesananFragment : Fragment() {
    private var _binding: FragmentDetailPesananBinding? = null
    private val binding: FragmentDetailPesananBinding get() = _binding!!

    private val args: DetailPesananFragmentArgs by navArgs()
    private val detailPesananViewModel: DetailPesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)
    private val pesananViewModel: PesananViewModel by hiltNavGraphViewModels(R.id.nav_pesanan)

    private lateinit var selectedProductAdapter: SelectedProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailPesananBinding.inflate(inflater, container, false)

        val item: PesananItem = args.pesanan
        if (detailPesananViewModel.pesanan.value?.idPesanan != item.idPesanan) {
            detailPesananViewModel.setData(item)
        }

        setupUI()
        updateUI()
        return binding.root
    }

    private fun setupUI() {

        binding.rvProduct.apply {
            this.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager.VERTICAL
                )
            )
            selectedProductAdapter = SelectedProductAdapter()
            this.adapter = selectedProductAdapter
            selectedProductAdapter.setDisabledButtonDelete(true)
            selectedProductAdapter.setOnItemClickListener(object :
                    SelectedProductAdapter.OnItemClickCallback {
                    override fun onDelete(item: DetailBarangItemPesanan) {
                    }
                })
        }

        binding.viewgroupPembayaran.setOnClickListener {
            findNavController().navigate(
                DetailPesananFragmentDirections.actionDetailPesananFragmentToPembayaranPesananFragment()
            )
        }
    }

    private fun updateUI() {
        detailPesananViewModel.pesanan.observe(viewLifecycleOwner) {
            pesananViewModel.refresh()
            Log.d(Companion.TAG, "updateUI: $it")

            binding.tvDipesan.text = it.tglDipesan
            binding.tvDiambil.text = it.tglDiambil
            binding.tvCustomerName.text = it.namaPemesan
            binding.tvCustomerPhone.text = it.noHpPemesan
            binding.tvCustomerAddress.text = it.alamat
            binding.tvOrderStatus.text = it.strStatus()

            val total: String = decimalFormat.format(it.total?.toInt())
            val dibayar: String = decimalFormat.format(it.dibayar?.toInt())
            binding.tvTotalS.text = getString(R.string.total_s, total)
            binding.tvDibayar.text = getString(R.string.rp, dibayar)

            if (it.dibayar!!.toInt() < it.total!!.toInt() || it.dibayar.toInt() == 0) {
                binding.tvStatus.text = "Belum lunas"
                binding.tvStatus.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.design_default_color_error
                    )
                )
            } else {
                binding.tvStatus.text = "Lunas"
            }

            selectedProductAdapter.submitList(it.detailBarang)

            val custPhone = it.noHpPemesan
            val text = """
                *Suplier PAO*
                
                ID Transaksi : ${it.idPesanan}
                Nama Pemesan : ${it.namaPemesan}
                Tanggal Diambil : ${it.tglDiambil}
                Produk : 
                    ${
            it.detailBarang?.joinToString { it ->
                "${it?.namaBarang}@${it?.hargaSatuan} X ${it?.jmlBarang} = *${
                decimalFormat.format(
                    it?.hargaSatuan?.toLong()?.times(it.jmlBarang?.toLong() ?: 1)
                )
                }*"
            }
            }
                *Total : ${decimalFormat.format(it.total.toLong())}*
            
                Status Pembayaran : *${
            if (it.dibayar < it.total) {
                "Kurang ${it.total.toLong().minus(it.dibayar.toLong())}"
            } else {
                "Lunas"
            }
            }*
                
            """.trimIndent()
            val message = URLEncoder.encode(text)
            binding.btnChatCustomer.setOnClickListener {
                val url = "https://wa.me/62$custPhone/?text=$message"
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "DetailPesananFragment"
    }
}
