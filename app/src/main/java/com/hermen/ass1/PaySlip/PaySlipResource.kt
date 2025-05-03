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
    val otherIncome: Double,

    // Deduction
    val incomeTax: Double,
    val unpaidLeave: Double,
    val otherDeduction: Double
) {

    val grossSalary: Double
        get() = basicSalary + overtimePay + allowance + bonus + otherIncome

    val deduction: Double
        get() = incomeTax + unpaidLeave + otherDeduction

    val netSalary: Double
        get() = grossSalary - deduction

}
