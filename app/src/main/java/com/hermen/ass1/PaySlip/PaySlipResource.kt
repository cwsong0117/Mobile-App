package com.hermen.ass1.PaySlip

data class PaySlipResource(
    val userId: String,
    val month: String,
    val year: String,
    // Income
    val basicSalary: Double,
    val allowance: Double,
    val bonus: Double,
    val overtimePay: Double,

    // Deduction
    val incomeTax: Double,
    val unpaidLeave: Double,
) {

    val grossSalary: Double
        get() = basicSalary + overtimePay + allowance + bonus

    val deduction: Double
        get() = incomeTax + unpaidLeave

    val netSalary: Double
        get() = grossSalary - deduction

}
