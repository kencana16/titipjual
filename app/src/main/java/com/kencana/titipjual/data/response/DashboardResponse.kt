package com.kencana.titipjual.data.response

import com.google.gson.annotations.SerializedName

data class DashboardResponse(

    @field:SerializedName("msg")
    val msg: String? = null,

    @field:SerializedName("data")
    val data: IncomeData? = null,

    @field:SerializedName("success")
    val success: Boolean? = null
)

data class IncomeData(

    @field:SerializedName("this_month_income")
    val thisMonthIncome: String? = null,

    @field:SerializedName("this_day_income")
    val thisDayIncome: String? = null
)
