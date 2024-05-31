package com.dezeta.guessit.domain.Repository

import android.database.sqlite.SQLiteException
import com.dezeta.guessit.domain.SerieDataBase
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess

class Repository {
    companion object {

        fun isLocalNewId(id: String): Boolean {
            val resultado = SerieDataBase.getInstance().GuessDao().selectAllLocal()
                .find { it.id.contains(id, ignoreCase = true) }
            return resultado == null

        }

        fun isLocalNewName(name: String): Boolean {
            val resultado = SerieDataBase.getInstance().GuessDao().selectAllLocal()
                .find { it.name.contains(name, ignoreCase = true) }
            return resultado == null
        }

        fun insertGuess(serie: Guess): Resource {
            return try {
                SerieDataBase.getInstance().GuessDao().insert(serie)
                Resource.Success<Guess>(serie)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun insertImages(images: List<Img>): Resource {
            return try {
                images.forEach {
                    SerieDataBase.getInstance().imgDao().insert(it)
                }
                Resource.Success(images)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun getLocalList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllLocal()
        }

        fun getSeriesList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllSerie()
        }
        fun getTestList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllTest()
        }
        fun getCountryList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllCountry()
        }

        fun getPlayerList(): List<Guess> {
            return SerieDataBase.getInstance().GuessDao().selectAllPlayer()
        }

        fun getSerieFromName(name: String): Guess {
            return SerieDataBase.getInstance().GuessDao().selectSerieFromName(name)
        }
        fun getGuessFromId(id: String): Guess {
            return SerieDataBase.getInstance().GuessDao().selectFromID(id)
        }

        fun deleteFromId(id: String): Resource {
            return try {
                SerieDataBase.getInstance().infoDao().deleteFromId(id)
                SerieDataBase.getInstance().imgDao().deleteFromId(id)
                SerieDataBase.getInstance().GuessDao().deleteFromId(id)
                Resource.Success(null)
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }

        fun getImageFromId(id: String): Resource {
            return try {
                Resource.Success(SerieDataBase.getInstance().imgDao().selectFromId(id))
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }
        fun getAnswerFromId(id: String): Resource {
            return try {
                Resource.Success(SerieDataBase.getInstance().AnswerTestDao().selectFromId(id))
            } catch (e: SQLiteException) {
                println(e.message)
                Resource.Error(e)
            }
        }


        fun getSerieName(): List<String>? {
            return try {
                SerieDataBase.getInstance().GuessDao().selectSerieName()
            } catch (e: SQLiteException) {
                println(e.message)
                null
            }
        }

        fun getImage0(id: String): Img? {
            var lista = SerieDataBase.getInstance().imgDao().selectFromId(id)
            var img: Img? = null
            lista.forEach { if (it.order == 0) img = it }
            return img
        }

        fun getInfoFromId(id: String): Info {
            return SerieDataBase.getInstance().infoDao().select(id)
        }

        fun getPlayerName(): List<String> {
            return listOf(
                "Diego Armando Maradona",
                "Lionel Messi",
                "Pelé",
                "Johan Cruyff",
                "Franz Beckenbauer",
                "Michel Platini",
                "Zinedine Zidane",
                "Ronaldo Nazário",
                "George Best",
                "Alfredo Di Stéfano",
                "Ferenc Puskás",
                "Marco van Basten",
                "Roberto Baggio",
                "Raheem Sterling",
                "Lev Yashin",
                "Ronaldinho",
                "Eusébio",
                "Bobby Charlton",
                "Garrincha",
                "Gerd Müller",
                "Ruud Gullit",
                "Jamie Vardy",
                "Jens Lehmann",
                "Deco",
                "Antonios Nikopolidis",
                "Mista",
                "Antoine Griezmann",
                "Maniche",
                "Rubén Baraja",
                "Ludovic Giuly",
                "Vicente",
                "Traianos Dellas",
                "Milan Baroš",
                "Angelos Charisteas",
                "Ruud van Nistelrooy",
                "Thierry Henry",
                "Andrés Iniesta",
                "Miroslav Klose",
                "Xavi Hernández",
                "Zico",
                "Kaká",
                "Rivaldo",
                "Romário",
                "Cristiano Ronaldo",
                "Neymar",
                "Steven Gerrard",
                "Kylian Mbappé",
                "Robert Lewandowski",
                "Sergio Agüero",
                "David Beckham",
                "Michael Owen",
                "Gianluigi Buffon",
                "Iker Casillas",
                "Fernando Hierro",
                "Fabio Cannavaro",
                "Alessandro Del Piero",
                "Francesco Totti",
                "Dino Zoff",
                "Gheorghe Hagi",
                "Hristo Stoichkov",
                "Juninho Pernambucano",
                "Luca Toni",
                "Samuel Eto'o",
                "John Terry",
                "Didier Drogba",
                "Lukas Podolski",
                "Patrick Vieira",
                "Marcos Senna",
                "Wayne Rooney",
                "Frank Lampard",
                "Frank Rijkaard",
                "Franck Ribéry",
                "Edwin van der Sar",
                "Clarence Seedorf",
                "Edgar Davids",
                "Dennis Bergkamp",
                "Jürgen Klinsmann",
                "Lothar Matthäus",
                "Oliver Kahn",
                "Rudi Völler",
                "Karl-Heinz Rummenigge",
                "Lothar Matthäus",
                "Roberto Carlos",
                "Cafu",
                "Rivaldo",
                "Andréi Arshavin",
                "Ivan Rakitic",
                "Luis Enrique",
                "Arturo Vidal",
                "Eden Hazard",
                "Alexis Sánchez",
                "Paul Pogba",
                "Thibaut Courtois",
                "Javier Mascherano",
                "Mario Götze",
                "Pavel Nedvěd",
                "Ricardo Carvalho",
                "Adriano",
                "Theodoros Zagorakis2",
                "Zlatan Ibrahimović",
                "Andriy Shevchenko",
                "Yaya Touré",
                "Fernando Torres",
                "Raúl González",
                "Xabi Alonso",
                "Fernando Redondo",
                "Juan Román Riquelme",
                "Gabriel Batistuta",
                "Paolo Rossi",
                "Gigi Riva",
                "Giuseppe Meazza",
                "Marco Tardelli",
                "Alessandro Altobelli",
                "Gianfranco Zola",
                "Roberto Donadoni",
                "Gianluca Vialli",
                "Dino Baggio",
                "Christian Vieri",
                "Alessandro Costacurta",
                "Filippo Inzaghi",
                "Andrea Pirlo",
                "Gennaro Gattuso",
                "Paolo Maldini",
                "Franco Baresi",
                "Marco Materazzi",
                "Francesco Totti",
                "Alessandro Del Piero",
                "Gianluigi Buffon",
                "Francesco Toldo",
                "Angelo Peruzzi",
                "Gianluca Pagliuca",
                "Dino Zoff",
                "Fabio Cannavaro",
                "Paolo Cannavaro",
                "Alessandro Nesta",
                "Marco Materazzi",
                "Andrea Barzagli",
                "Giorgio Chiellini",
                "Leonardo Bonucci",
                "Gianluca Zambrotta",
                "Mauro Camoranesi",
                "Claudio Marchisio",
                "Andrea Pirlo",
                "Daniele De Rossi",
                "Alberto Aquilani",
                "Simone Perrotta",
                "Francesco Totti",
                "Alessandro Del Piero",
                "Gianluigi Buffon",
                "Francesco Toldo",
                "Angelo Peruzzi",
                "Gianluca Pagliuca",
                "Dino Zoff",
                "Alessandro Nesta",
                "Marco Materazzi",
                "Fabio Grosso",
                "Andrea Barzagli",
                "Giorgio Chiellini",
                "Erling Haaland",
                "Kylian Mbappé",
                "Karim Benzema",
                "Neymar",
                "Robert Lewandowski",
                "Phil Foden",
                "Jadon Sancho",
                "Bruno Fernandes",
                "Mohamed Salah",
                "Jorginho",
                "Kylian Mbappé",
                "Cristiano Ronaldo",
                "Kevin De Bruyne",
                "Phil Foden",
                "Sergio Ramos",
                "Joshua Kimmich",
                "Luka Modric",
                "Virgil van Dijk",
                "Harry Kane",
                "Son Heung-min",
                "Romelu Lukaku",
                "Gianluigi Donnarumma",
                "Frenkie de Jong",
                "Jack Grealish",
                "Sadio Mané",
                "Bukayo Saka",
                "Carles Puyol",
                "Wilfried Zaha",
                "Riyad Mahrez",
                "Pepe",
                "Rui Patricio",
                "Pierre Emerick Aubameyang",
                "Marcus Rashford",
                "Vinícius Júnior",
                "Trent Alexander-Arnold",
                "Thomas Müller",
                "Toni Kroos",
                "Georginio Wijnaldum",
                "Kalidou Koulibaly",
                "Donny van de Beek",
                "Marc-André Ter Stegen",
                "Dušan Tadić",
                "Dimitri Payet",
                "Alisson Becker",
                "Jan Oblak",
                "Manuel Neuer",
                "Marcelo",
                "Gerard Piqué",
                "David Villa",
                "Radamel Falcao",
                "Marcos Llorente",
                "João Cancelo",
                "Andrew Robertson",
                "Arjen Robben",
                "James Rodríguez",
                "Ángel Di María",
                "Diego Costa",
                "Thomas Müller",
                "Bastian Schweinsteiger",
                "Cesc Fàbregas",
                "Philipp Lahm",
                "Dani Alves",
                "Marquinhos",
                "Lautaro Martínez",
                "Martin Odegaard",
                "Lucas Pérez",
                "Gerard Moreno",
                "Roger Martí",
                "Ansu Fati",
                "Fede Valverde",
                "Ferran Torres",
                "Salisu",
                "Takefusa Kubo",
                "Marc Cucurella",
                "Ángel Rodríguez",
                "Antonio Puertas",
                "Darwin Machís",
                "Domingos Duarte",
                "Gareth Bale",
                "Kun Agüero",
                "Dušan Vlahović",
                "Luis Díaz",
                "Nacho",
                "Gavi",
                "Pedri",
                "Jude Bellingham",
                "Luis Suárez",
                "Fabinho",
                "Rafael Leão",
                "Jordi Alba",
                "Casemiro",
                "Koke",
                "Hugo Lloris",
                "Gonzalo Higuaín",
                "Matthijs de Ligt",
                "Bernardo Silva",
                "Diego Godín",
                "Paulo Dybala",
                "Alisson Becker",
                "Mario Mandžukić",
                "Isco Alarcón",
                "Kylian Mbappé",
                "Raphaël Varane",
                "N'Golo Kanté",
                "Dani Olmo",
                "Iñaki Williams",
                "José Campaña",
                "Mikel Merino",
                "Yuri Berchiche",
                "João Félix",
                "Jules Koundé"
            )
        }




    }
}