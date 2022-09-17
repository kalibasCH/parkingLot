package com.example.parkinglot

import kotlin.system.exitProcess

fun main() {
    StartOfWork(false).start()
}

private class CreateTheParking {
    fun create(amountOfPlaces: Int): MutableList<ParkingPlace> {
        val parking = mutableListOf<ParkingPlace>()
        for (i in 1..amountOfPlaces) {
            val freePlaces = ParkingPlace()
            freePlaces.place = i.toString()
            parking.add(freePlaces)
        }
        return parking
    }
}

private class StartOfWork(var parkingIsCreated: Boolean) {
    var parking = mutableListOf<ParkingPlace>()
    fun start() {
        val dataString = ReadingData().inputDataString()
        if (parkingIsCreated) {
            when (dataString.split(" ").first()) {
                "create" -> {
                    val amountOfPlaces = dataString.split(" ").last()
                    parking = CreateTheParking().create(amountOfPlaces.toInt())
                    Bot(sizeParking = amountOfPlaces.toInt()).notificationAboutCreateTheParking()
                    parkingIsCreated = true
                    start()
                }
                "exit" -> exitProcess(0)
                "park" -> {
                    val resultOfParking = ParkingOn(dataString, parking).placeAndColorPuttingCar()
                    if (resultOfParking.length > 1) {
                        val (place, color) = resultOfParking.split(" ")
                        Bot(color, place).parkingTheCarMessage()
                        start()
                    } else {
                        Bot().notificationAboutFullParking()
                        start()
                    }
                }
                "leave" -> {
                    val place = dataString.split(" ").last()
                    if (Leaving(place, parking).findTheCar()) {
                        Bot(place = place).notificationAboutFreePlaceMessage()
                        start()
                    } else {
                        Bot(place = place).theCarIsNotFoundMessage()
                        start()
                    }
                }
                "status" -> {
                    if (GetAllCarsOnHisPlace(parking).getPlaceCarAndColor().size > 0) {
                        val listOfCar = GetAllCarsOnHisPlace(parking).getPlaceCarAndColor()
                        for (i in listOfCar.indices) {
                            Bot(
                                color = listOfCar[i].colorOfCar,
                                place = listOfCar[i].place,
                                numberOfCar = listOfCar[i].serNumberOfCar
                            ).showAllCars()
                        }
                    } else {
                        Bot().notificationAboutEmptyParking()
                    }
                    start()
                }
                "spot_by_color" -> {
                    val colorOfCar = dataString.split(" ").last().uppercase()
                    val listOfSpot = GetAllCarsOnHisPlace(parking).getSpotByColor(colorOfCar)
                    if (listOfSpot.size > 0) {
                        println(listOfSpot.joinToString(", "))
                    } else {
                        Bot(color = colorOfCar).notificationAboutNoCarWithColor()
                    }
                    start()
                }
                "spot_by_reg" -> {
                    val regOfCar = dataString.split(" ").last()
                    val listOfSpot = GetAllCarsOnHisPlace(parking).getSpotByReg(regOfCar)
                    if (listOfSpot.size > 0) {
                        println(listOfSpot.joinToString(", "))
                    } else {
                        Bot(numberOfCar = regOfCar).notificationAboutNoCarWithReg()
                    }
                    start()
                }
                "reg_by_color" -> {
                    val colorOfCar = dataString.split(" ").last().uppercase()
                    val listOfSpot = GetAllCarsOnHisPlace(parking).getRegNumByColor(colorOfCar)
                    if (listOfSpot.size > 0) {
                        println(listOfSpot.joinToString(", "))
                    } else {
                        Bot(color = colorOfCar).notificationAboutNoCarWithColor()
                    }
                    start()
                }
            }
        } else {
            when (dataString.split(" ").first()) {
                "create" -> {
                    val amountOfPlaces = dataString.split(" ").last()
                    parking = CreateTheParking().create(amountOfPlaces.toInt())
                    Bot(sizeParking = amountOfPlaces.toInt()).notificationAboutCreateTheParking()
                    parkingIsCreated = true
                    start()
                }
                "exit" -> exitProcess(0)
                else -> {
                    Bot().notificationAboutNoTheParking()
                    start()
                }
            }
        }
    }
}


private class GetAllCarsOnHisPlace(val parking: MutableList<ParkingPlace>) {

    val listOfCar = mutableListOf<ParkingPlace>()

    val listOfSpot = mutableListOf<Int>()

    val listOfNum = mutableListOf<String>()

    fun getPlaceCarAndColor(): MutableList<ParkingPlace> {
        for (carOnPlace in parking.indices) {
            if (!parking[carOnPlace].isEmpty) {
                listOfCar.add(parking[carOnPlace])
            }
        }
        return listOfCar
    }

    fun getSpotByColor(colorOfCar: String): MutableList<Int> {
        for (carOnPlace in parking.indices) {
            if (parking[carOnPlace].colorOfCar == colorOfCar) {
                listOfSpot.add(parking[carOnPlace].place.toInt())
            }
        }
        return listOfSpot
    }

    fun getSpotByReg(numberOfCar: String): MutableList<Int> {
        for (carOnPlace in parking.indices) {
            if (parking[carOnPlace].serNumberOfCar == numberOfCar) {
                listOfSpot.add(parking[carOnPlace].place.toInt())
            }
        }
        return listOfSpot
    }

    fun getRegNumByColor(colorOfCar: String): MutableList<String> {
        for (carOnPlace in parking.indices) {
            if (parking[carOnPlace].colorOfCar == colorOfCar) {
                listOfNum.add(parking[carOnPlace].serNumberOfCar)
            }
        }
        return listOfNum
    }

}

private class ParkingOn(val dataString: String, val parking: MutableList<ParkingPlace>) {
    fun placeAndColorPuttingCar(): String {
        val serNum = dataString.split(" ")[1]
        val color = dataString.split(" ")[2].uppercase()
        var placeAndColorPuttingCar = ""
        for (freePlace in parking.indices) {
            if (parking[freePlace].isEmpty) {
                parking[freePlace].isEmpty = false
                parking[freePlace].serNumberOfCar = serNum
                parking[freePlace].colorOfCar = color
                placeAndColorPuttingCar =
                    (freePlace + 1).toString() + " " + parking[freePlace].colorOfCar
                break
            }
        }
        return placeAndColorPuttingCar
    }
}

private class Leaving(private val place: String, val parking: MutableList<ParkingPlace>) {
    fun findTheCar(): Boolean {
        var resultFinding = false

        if (!parking[place.toInt() - 1].isEmpty) {
            resultFinding = true
            parking[place.toInt() - 1].isEmpty = true
            parking[place.toInt() - 1].serNumberOfCar = ""
            parking[place.toInt() - 1].colorOfCar = ""
        }
        return resultFinding
    }
}

private class ReadingData {
    fun inputDataString(): String {
        return readln()
    }
}

private class Bot(
    val color: String = "",
    val place: String = "",
    val sizeParking: Int = 0,
    val numberOfCar: String = ""
) {
    fun notificationAboutCreateTheParking() {
        println("Created a parking lot with $sizeParking spots.")
    }
    fun notificationAboutNoTheParking() {
        println("Sorry, a parking lot has not been created.")
    }
    fun parkingTheCarMessage() {
        println("$color car parked in spot $place.")
    }
    fun theCarIsNotFoundMessage() { // здесь авто отсутствует
        println("There is no car in spot $place.")
    }
    fun notificationAboutFreePlaceMessage() { // авто выехало, теперь место свободно
        println("Spot $place is free.")
    }
    fun notificationAboutFullParking() {
        println("Sorry, the parking lot is full.")
    }
    fun notificationAboutEmptyParking() {
        println("Parking lot is empty.")
    }
    fun showAllCars() {
        println("$place $numberOfCar $color")
    }
    fun notificationAboutNoCarWithColor() {
        println("No cars with color $color were found.")
    }
    fun notificationAboutNoCarWithReg() {
        println("No cars with registration number $numberOfCar were found.")
    }

}

private class ParkingPlace(
    var place: String = "",
    var isEmpty: Boolean = true,
    var serNumberOfCar: String = "",
    var colorOfCar: String = ""
)