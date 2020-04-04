package com.kobylinski.dusigrosz.model

class Debeter(val id:Int,val name: String, var phone: String? = "", var debt: Double = 0.0) {

    fun returnDebt(ret: Double) {
        debt = -ret;
    }

    fun addDebt(ad: Double) {
        debt += ad;
    }

    fun changePhoneNr(phone: String) {
        this.phone = phone
    }
    companion object{
        val names = arrayListOf<Debeter>(
            Debeter(1,"Jacek Wolski", "221-222-433-433", 23.23),
            Debeter(2,"Donald Trump", "434-545-544", 234.22),
            Debeter(3,"Midas", "123-343-433", 123.43),
            Debeter(4,"Mieszko", "123-433-544", 435.6)
        )
        private var debtsSum= names.sumByDouble { it.debt }

        fun getAllDebts(): Double {
            return debtsSum
        }
    }
}