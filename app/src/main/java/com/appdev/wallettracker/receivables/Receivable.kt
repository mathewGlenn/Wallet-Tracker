package com.appdev.wallettracker.receivables

class Receivable {
    var name: String? = null
    var description: String? = null
    var amount: Float? = null
    var date: Long? = null
    var remainingBalance: Float? = null
    var isCollected: Boolean? = null

    constructor() {}
    constructor(
        name: String?,
        amount: Float?,
        description: String?,
        date: Long?,
        remainingBalance: Float?,
        isCollected: Boolean?,
    ) {
        this.name = name
        this.amount = amount
        this.description = description
        this.date = date
        this.remainingBalance = remainingBalance
        this.isCollected = isCollected
    }
}