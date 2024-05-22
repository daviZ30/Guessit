package com.dezeta.guessit.domain


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dezeta.guessit.domain.Dao.ImgDao
import com.dezeta.guessit.domain.Dao.InfoDao
import com.dezeta.guessit.domain.Dao.GuessDao
import com.dezeta.guessit.domain.converter.CategoryConverter
import com.dezeta.guessit.domain.converter.DifficultyConverter
import com.dezeta.guessit.domain.converter.GuessTypeConverter
import com.dezeta.guessit.domain.converter.InstantConverter
import com.dezeta.guessit.domain.entity.Category
import com.dezeta.guessit.domain.entity.Difficulty
import com.dezeta.guessit.domain.entity.Img
import com.dezeta.guessit.domain.entity.Info
import com.dezeta.guessit.domain.entity.Guess
import com.dezeta.guessit.domain.entity.GuessType
import com.dezeta.guessit.utils.Locator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Instant

@Database(
    entities = [Img::class, Info::class, Guess::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    GuessTypeConverter::class,
    InstantConverter::class,
    CategoryConverter::class,
    DifficultyConverter::class,
)
abstract class SerieDataBase : RoomDatabase() {

    abstract fun imgDao(): ImgDao
    abstract fun infoDao(): InfoDao
    abstract fun GuessDao(): GuessDao


    companion object {
        @Volatile
        private var INSTANCE: SerieDataBase? = null
        fun getInstance(): SerieDataBase {
            return INSTANCE ?: synchronized(SerieDataBase::class) {
                val instance = buildDatabase()
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(): SerieDataBase {
            return Room.databaseBuilder(
                Locator.requiredApplication, SerieDataBase::class.java, "Serie"
            ).fallbackToDestructiveMigration().allowMainThreadQueries()
                .addTypeConverter(InstantConverter())
                .addTypeConverter(CategoryConverter())
                .addTypeConverter(DifficultyConverter())
                .addTypeConverter(GuessTypeConverter())
                .addCallback(
                    RoomDbInitializer(INSTANCE)
                ).build()
        }
    }

    class RoomDbInitializer(val instance: SerieDataBase?) : RoomDatabase.Callback() {
        private val applicationScope = CoroutineScope(SupervisorJob())

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            applicationScope.launch(Dispatchers.IO) {
                populateDatabase()
            }
        }

        private fun SetFecha(fecha: String): Instant {
            val dateString = fecha + "T00:00:00Z"
            val instant = Instant.parse(dateString)
            return instant
        }

        private fun populateDatabase() {
            getInstance().let { database ->
                with(database) {
                    GuessDao().insert(Guess("online1","Stranger Things", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online1", "https://i.postimg.cc/DfGqDkrz/stranger0.jpg", 0))
                    imgDao().insert(Img("online1", "https://i.postimg.cc/4xfVmYrK/Stranger1.png", 1))
                    imgDao().insert(Img("online1", "https://i.postimg.cc/k5DSvkMq/Stranger2.webp", 2))
                    imgDao().insert(Img("online1", "https://i.postimg.cc/ZKF6b8Dt/Stranger3.jpg", 3))
                    infoDao().insert(Info("online1", 8.7, SetFecha("2016-07-15"),
                            "En Hawkins, Indiana, en noviembre de 1983, un grupo de niños se enfrenta a lo desconocido cuando su amigo Will Byers desaparece misteriosamente. Mientras su madre, Joyce, busca desesperadamente a Will, los amigos de este, Mike, Dustin y Lucas, descubren a una niña con habilidades psicocinéticas llamada Once. Juntos, se aventuran en un mundo oscuro y peligroso conocido como el Mundo del Revés, donde criaturas aterradoras acechan. A medida que desentrañan los secretos del laboratorio local y luchan contra el Demogorgon, se enfrentan a desafíos sobrenaturales y amistades inquebrantables."
                        )
                    )

                    GuessDao().insert(Guess("online2","Breaking Bad", Difficulty.Easy, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online2", "https://i.postimg.cc/4yW2qDcw/breaking0.jpg", 0))
                    imgDao().insert(Img("online2", "https://i.postimg.cc/vZg22SZL/breaking1.png", 1))
                    imgDao().insert(Img("online2", "https://i.postimg.cc/52h7hqHc/breaking2.png", 2))
                    imgDao().insert(Img("online2", "https://i.postimg.cc/ncwwktph/breaking3.png", 3))
                    infoDao().insert(Info("online2", 9.5, SetFecha("2008-01-20"),
                            "Breaking Bad es una serie de drama y crimen que sigue la transformación de Walter White, un profesor de química de secundaria en Albuquerque, Nuevo México. Después de ser diagnosticado con cáncer de pulmón en etapa 3 y con solo dos años de vida, Walter se asocia con un exalumno para cocinar metanfetamina cristalina como medio de apoyo para su familia. A medida que se adentra en el mundo del crimen, su alter ego, Heisenberg, emerge, y la serie explora temas de poder, moralidad y consecuencias. La actuación magistral de Bryan Cranston como Walter White y la narrativa intensa hacen de Breaking Bad una de las series más icónicas de la televisión."
                        )
                    )

                    GuessDao().insert(Guess("online3","Game of Thrones", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online3", "https://i.postimg.cc/Fs2VzFh3/game0.jpg", 0))
                    imgDao().insert(Img("online3", "https://i.postimg.cc/GmGJy0pN/gameof1.jpg", 1))
                    imgDao().insert(Img("online3", "https://i.postimg.cc/WbD7sqcs/gameof2.jpg", 2))
                    imgDao().insert(Img("online3", "https://i.postimg.cc/Wzd0pCK5/gameof3.png", 3))
                    infoDao().insert(Info("online3", 9.2, SetFecha("2011-04-17"),
                        "En el continente de Poniente, varias casas nobles luchan por el Trono de Hierro, el símbolo del poder absoluto. La historia sigue a personajes como Eddard Stark, Daenerys Targaryen, Jon Snow, Tyrion Lannister y muchos otros mientras se enfrentan a traiciones, alianzas cambiantes y oscuros secretos. La serie está repleta de giros inesperados, personajes complejos y una ambientación medieval fascinante"
                        )
                    )

                    GuessDao().insert(Guess("online4","Peaky Blinders", Difficulty.Easy, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online4", "https://i.postimg.cc/wjN8RBLp/peaky0.jpg", 0))
                    imgDao().insert(Img("online4", "https://i.postimg.cc/9Mnqk1MZ/peaky1.webp", 1))
                    imgDao().insert(Img("online4", "https://i.postimg.cc/CM9cBsNR/peaky2.webp", 2))
                    imgDao().insert(Img("online4", "https://i.postimg.cc/sg4mLdRW/peaky3.png", 3))
                    infoDao().insert(Info("online4", 8.8, SetFecha("2013-09-12"),
                        "La trama se desarrolla en la ciudad de Birmingham, justo después de la Primera Guerra Mundial. La pandilla de los Peaky Blinders, liderada por Tommy Shelby (interpretado por Cillian Murphy), busca el poder y la dominación en un mundo lleno de intrigas y peligros. Los Shelby son conocidos por sus gorras con cuchillas ocultas y su estilo de vida audaz."
                        )
                    )

                    GuessDao().insert(Guess("online5","Vikings", Difficulty.Easy, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online5", "https://i.postimg.cc/J03Hh8xc/vikingos0.jpg", 0))
                    imgDao().insert(Img("online5", "https://i.postimg.cc/t4vZJ23f/vikings1.png", 1))
                    imgDao().insert(Img("online5", "https://i.postimg.cc/tC5VQXY9/Vikingos2.png", 2))
                    imgDao().insert(Img("online5", "https://i.postimg.cc/x11N9WzM/vikingos3.png", 3))
                    infoDao().insert(Info("online5", 8.5, SetFecha("2013-06-03"),
                            "La trama se centra en Ragnar Lothbrok, un legendario guerrero vikingo que busca expandir su influencia y riqueza. A medida que lidera incursiones en Inglaterra y otros territorios, enfrenta desafíos tanto en el campo de batalla como en su vida personal. La serie explora temas como la lealtad, la traición, la religión y la ambición."
                        )
                    )

                    GuessDao().insert(Guess("online6","Chernobyl", Difficulty.Easy, Category.Historical_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online6", "https://imagizer.imageshack.com/img924/6117/0oPcqT.jpg", 0))
                    imgDao().insert(Img("online6", "https://imagizer.imageshack.com/img923/9194/AuigFD.jpg", 1))
                    imgDao().insert(Img("online6", "https://imagizer.imageshack.com/img924/8702/kvwX3Y.jpg", 2))
                    imgDao().insert(Img("online6", "https://imagizer.imageshack.com/img922/3899/9NedXl.jpg", 3))
                    infoDao().insert(Info("online6", 9.3, SetFecha("2019-08-06"),
                            "Chernóbil es una miniserie de televisión que dramatiza los eventos en torno al desastre nuclear ocurrido en Chernóbil en abril de 1986. La serie se centra en los esfuerzos de limpieza sin precedentes que siguieron al colapso del reactor 4 de la planta nuclear. A través de cinco capítulos, la historia muestra las historias de los bomberos, voluntarios y equipos de mineros que arriesgaron sus vidas para contener la catástrofe. Basada en los recuerdos locales de Prípiat, la serie ofrece una experiencia intensa y conmovedora sobre uno de los eventos más impactantes de la historia moderna."
                        )
                    )

                    GuessDao().insert(Guess("online7","The Crown", Difficulty.Medium, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online7", "https://imagizer.imageshack.com/img924/6451/N2WM0C.jpg", 0))
                    imgDao().insert(Img("online7", "https://imagizer.imageshack.com/img924/538/wtnVld.png", 1))
                    imgDao().insert(Img("online7", "https://imagizer.imageshack.com/img923/7650/INzIVP.png", 2))
                    imgDao().insert(Img("online7", "https://imagizer.imageshack.com/img924/8360/UZSxou.png", 3))
                    infoDao().insert(Info("online7", 8.6, SetFecha("2016-11-01"),
                            "The Crown es una serie dramática creada por Peter Morgan que narra la vida de la Reina Isabel II y su reinado desde sus inicios hasta la actualidad. A lo largo de las temporadas, exploramos los desafíos, intrigas y cambios significativos que enfrenta la monarquía británica. La serie ofrece una visión fascinante de la historia y la política detrás del trono, con un reparto excepcional y una cuidada ambientación. Si te interesan las historias de la realeza y la política, The Crown es una elección imperdible."
                        )
                    )

                    GuessDao().insert(Guess("online8","Sons of Anarchy", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online8", "https://imagizer.imageshack.com/img922/4563/Acn4DP.jpg", 0))
                    imgDao().insert(Img("online8", "https://imagizer.imageshack.com/img922/3627/vkfpvy.jpg", 1))
                    imgDao().insert(Img("online8", "https://imagizer.imageshack.com/img922/6123/YUomcT.png", 2))
                    imgDao().insert(Img("online8", "https://imagizer.imageshack.com/img924/9828/qbqXw6.png", 3))
                    infoDao().insert(Info("online8", 8.6, SetFecha("2008-09-03"),
                            "Sons of Anarchy es una serie de televisión estadounidense creada por Kurt Sutter. La trama sigue a la pandilla de motociclistas Sons of Anarchy Motorcycle Club, Redwood Original (SAMCRO) en la ficticia ciudad de Charming, California. Liderados por Jax Teller (interpretado por Charlie Hunnam), los miembros de SAMCRO se enfrentan a rivalidades, lealtades divididas y conflictos internos mientras luchan por mantener el control de su territorio y su estilo de vida. La serie combina acción, drama y elementos de tragedia griega, y se desarrolla en un mundo lleno de motos, violencia y secretos oscuros. Si te gustan las historias sobre lealtad, crimen organizado y personajes complejos, Sons of Anarchy es una opción emocionante. "
                        )
                    )

                    GuessDao().insert(Guess("online9","Dexter", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online9", "https://imagizer.imageshack.com/img923/8923/b0hhBY.jpg", 0))
                    imgDao().insert(Img("online9", "https://imagizer.imageshack.com/img923/4692/6TFwZ6.jpg", 1))
                    imgDao().insert(Img("online9", "https://imagizer.imageshack.com/img922/6652/bErunV.jpg", 3))
                    imgDao().insert(Img("online9", "https://imagizer.imageshack.com/img922/6535/OfON2x.png", 2))
                    infoDao().insert(Info("online9", 8.6, SetFecha("2006-10-01"),
                            "Dexter es una serie de televisión estadounidense que combina elementos de drama, suspenso y crimen. La trama sigue a Dexter Morgan, un forense especializado en análisis de salpicaduras de sangre en el Departamento de Policía de Miami, pero también es un asesino en serie. Dexter tiene un código moral muy particular: solo mata a otros asesinos que han escapado de la justicia. La serie explora su doble vida, sus relaciones personales y su lucha interna entre su lado oscuro y su deseo de ser parte de la sociedad. A lo largo de las temporadas, vemos cómo Dexter enfrenta desafíos, investiga crímenes y mantiene su secreto mientras intenta llevar una vida normal. Si te gustan los personajes complejos y las tramas intrigantes, Dexter es una excelente opción. "
                        )
                    )

                    GuessDao().insert(Guess("online10","The Walking Dead", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online10", "https://imagizer.imageshack.com/img923/5372/vHR7a9.jpg", 0))
                    imgDao().insert(Img("online10", "https://imagizer.imageshack.com/img924/9441/t6e2dg.jpg", 1))
                    imgDao().insert(Img("online10", "https://imagizer.imageshack.com/img923/9839/JKzblO.jpg", 2))
                    imgDao().insert(Img("online10", "https://imagizer.imageshack.com/img922/2880/BpDrjn.png", 3))
                    infoDao().insert(Info("online10", 8.1, SetFecha("2010-10-31"),
                            "The Walking Dead es una serie de televisión estadounidense que combina elementos de drama, suspenso y crimen. La trama sigue a Dexter Morgan, un forense especializado en análisis de salpicaduras de sangre en el Departamento de Policía de Miami, pero también es un asesino en serie. Dexter tiene un código moral muy particular: solo mata a otros asesinos que han escapado de la justicia. La serie explora su doble vida, sus relaciones personales y su lucha interna entre su lado oscuro y su deseo de ser parte de la sociedad. A lo largo de las temporadas, vemos cómo Dexter enfrenta desafíos, investiga crímenes y mantiene su secreto mientras intenta llevar una vida normal. Si te gustan los personajes complejos y las tramas intrigantes, Dexter es una excelente opción."
                        )
                    )


                    GuessDao().insert(Guess("online11","Lost", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online11", "https://imagizer.imageshack.com/img922/9593/DWksne.png", 0))
                    imgDao().insert(Img("online11", "https://imagizer.imageshack.com/img922/3241/rQ9dYq.jpg", 1))
                    imgDao().insert(Img("online11", "https://imagizer.imageshack.com/img922/6756/Blvx3t.png", 2))
                    imgDao().insert(Img("online11", "https://imagizer.imageshack.com/img924/3180/myQ2Xw.png", 3))
                    infoDao().insert(Info("online11", 8.3, SetFecha("2004-09-22"),
                            "Lost es una serie de televisión estadounidense que combina elementos de drama, misterio, ciencia ficción y aventura. La trama sigue a un grupo de sobrevivientes de un accidente de avión que quedan varados en una misteriosa isla tropical. A medida que luchan por sobrevivir, descubren secretos oscuros, criaturas extrañas y una serie de eventos inexplicables. La serie se centra en los personajes, sus relaciones y sus pasados entrelazados, mientras exploran la isla y enfrentan desafíos tanto físicos como emocionales. Lost es conocida por su narrativa no lineal, giros sorprendentes y un final que generó debates entre los fanáticos. Si te gustan las historias intrigantes y llenas de enigmas, esta serie es una excelente opción."
                        )
                    )


                    GuessDao().insert(Guess("online12","Better Call Saul", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online12", "https://imagizer.imageshack.com/img923/2698/GMLpfu.jpg", 0))
                    imgDao().insert(Img("online12", "https://imagizer.imageshack.com/img924/6807/Lw5nze.png", 1))
                    imgDao().insert(Img("online12", "https://imagizer.imageshack.com/img924/4223/ycbUXe.jpg", 2))
                    imgDao().insert(Img("online12", "https://imagizer.imageshack.com/img923/6126/u7vTpc.jpg", 3))
                    infoDao().insert(Info("online12", 9.0, SetFecha("2015-02-08"),
                            "Better Call Saul es una serie de televisión que sirve como precuela de la exitosa serie “Breaking Bad”. La trama se centra en el personaje del abogado Saul Goodman (interpretado por Bob Odenkirk) seis años antes de su encuentro con Walter White. La historia sigue la transformación de Jimmy McGill, un picapleitos de poca monta con problemas económicos, mientras se convierte en el abogado criminalista Saul Goodman. A medida que Jimmy lucha por sobrevivir en un mundo implacable, descubrimos los motivos y la evolución que lo llevan a adoptar la personalidad extravagante y astuta de Saul Goodman"
                        )
                    )


                    GuessDao().insert(Guess("online13","Dark", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online13", "https://imagizer.imageshack.com/img923/8017/5DIe6i.jpg", 0))
                    imgDao().insert(Img("online13", "https://imagizer.imageshack.com/img923/7185/yzxfVj.png", 1))
                    imgDao().insert(Img("online13", "https://imagizer.imageshack.com/img922/9651/kKHOBP.png", 2))
                    imgDao().insert(Img("online13", "https://imagizer.imageshack.com/img924/5725/m9OCGC.jpg", 3))
                    infoDao().insert(Info("online13", 8.7, SetFecha("2017-11-20"),
                        "Dark es una serie alemana de ciencia ficción creada por Baran bo Odar y Jantje Friese. La trama se desarrolla en la pequeña ciudad ficticia de Winden, donde cuatro familias están conectadas por secretos oscuros y viajes en el tiempo. A medida que los personajes exploran misterios, paradojas temporales y relaciones complejas, descubrimos que la línea entre pasado, presente y futuro es más delgada de lo que imaginamos. Dark es conocida por su narrativa intrincada, atmósfera sombría y giros sorprendentes. Si te gustan las historias complejas y enigmáticas, esta serie es una excelente elección."
                        )
                    )
                    GuessDao().insert(Guess("online14","The Good Doctor", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online14", "https://imagizer.imageshack.com/img924/5052/kyFfVg.png", 0))
                    imgDao().insert(Img("online14", "https://imagizer.imageshack.com/img924/6535/Bs3xLH.png", 1))
                    imgDao().insert(Img("online14", "https://imagizer.imageshack.com/img924/2766/G5vXnQ.jpg", 2))
                    imgDao().insert(Img("online14", "https://imagizer.imageshack.com/img922/74/X0gWyr.jpg", 3))
                    infoDao().insert(Info("online14", 8.0, SetFecha("2017-09-25"),
                        "La serie sigue a Shaun Murphy, un joven cirujano con autismo y síndrome del sabio, mientras trabaja en el ficticio hospital St. Bonaventure en San José, California. A pesar del escepticismo inicial de sus colegas, Shaun demuestra su valía con métodos milagrosos. La serie se estrenó en 2017 y ha sido un fenómeno televisivo en EE. UU."
                        )
                    )

                    GuessDao().insert(Guess("country1","Spain", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country1", "https://i.postimg.cc/3J5HH3Rw/espanha1.png", 1))
                    imgDao().insert(Img("country1", "https://i.postimg.cc/44hTMYXM/espanha2.png", 2))
                    imgDao().insert(Img("country1", "https://i.postimg.cc/VLqPf2rW/espanha3.png", 3))

                    GuessDao().insert(Guess("country2","Argelia", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country2", "https://i.postimg.cc/RFVjX0XP/argelia1.png", 1))
                    imgDao().insert(Img("country2", "https://i.postimg.cc/5yXDWRcL/argelia2.png", 2))
                    imgDao().insert(Img("country2", "https://i.postimg.cc/nrNgFGZ5/argelia3.png", 3))

                    GuessDao().insert(Guess("country3","Angola", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country3", "https://i.postimg.cc/Gh8nWHrV/angola1.png", 1))
                    imgDao().insert(Img("country3", "https://i.postimg.cc/fTGQDDRL/angola2.png", 2))
                    imgDao().insert(Img("country3", "https://i.postimg.cc/R09rJdfx/angola3.png", 3))

                    GuessDao().insert(Guess("country4","Egipto", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country4", "https://i.postimg.cc/tT8GJjN2/egipto1.png", 1))
                    imgDao().insert(Img("country4", "https://i.postimg.cc/T1mMW4kG/egipto2.png", 2))
                    imgDao().insert(Img("country4", "https://i.postimg.cc/8zTQjwLQ/egiptp3.png", 3))

                    GuessDao().insert(Guess("country5","Sudáfrica", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country5", "https://i.postimg.cc/W1z8Wkvq/sudaf1.png", 1))
                    imgDao().insert(Img("country5", "https://i.postimg.cc/yYVvRGpP/suda2.png", 2))
                    imgDao().insert(Img("country5", "https://i.postimg.cc/GhqzxjZ1/suda3.png", 3))

                    GuessDao().insert(Guess("country6","Canadá", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country6", "https://i.postimg.cc/hvQZ5Qx0/canada1.jpg", 1))
                    imgDao().insert(Img("country6", "https://i.postimg.cc/ncBpsC57/canada2.png", 2))
                    imgDao().insert(Img("country6", "https://i.postimg.cc/Y2Jtq1XM/canada3.png", 3))

                    GuessDao().insert(Guess("country7","Estados Unidos", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country7", "https://i.postimg.cc/ZqKSDjqF/eeuu1.png", 1))
                    imgDao().insert(Img("country7", "https://i.postimg.cc/TP768mL8/eeuu2.png", 2))
                    imgDao().insert(Img("country7", "https://i.postimg.cc/j5DrR5PD/eeuu3.png", 3))

                    GuessDao().insert(Guess("country8","Brasil", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country8", "https://i.postimg.cc/XJzDLkZJ/brasil1.png", 1))
                    imgDao().insert(Img("country8", "https://i.postimg.cc/hjj5V5t6/brasil2.png", 2))
                    imgDao().insert(Img("country8", "https://i.postimg.cc/pyNcbVJM/brasil3.png", 3))

                    GuessDao().insert(Guess("country9","México", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country9", "https://i.postimg.cc/SxxTBPWs/mexico1.jpg", 1))
                    imgDao().insert(Img("country9", "https://i.postimg.cc/1X07ShCg/mexico2.png", 2))
                    imgDao().insert(Img("country9", "https://i.postimg.cc/66CmnnW9/mexico3.png", 3))

                    GuessDao().insert(Guess("country10","Argentina", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country10", "https://i.postimg.cc/DybRCHPK/argen1.png", 1))
                    imgDao().insert(Img("country10", "https://i.postimg.cc/htW6Ns2Q/argen2.png", 2))
                    imgDao().insert(Img("country10", "https://i.postimg.cc/dV2pFCfH/argen3.png", 3))

                    GuessDao().insert(Guess("country11","China", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country11", "https://i.postimg.cc/VsSY87WK/china1.png", 1))
                    imgDao().insert(Img("country11", "https://i.postimg.cc/zGN5TqZQ/china2.png", 2))
                    imgDao().insert(Img("country11", "https://i.postimg.cc/qvGrLWpv/china3.png", 3))

                    GuessDao().insert(Guess("country12","India", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country12", "https://i.postimg.cc/HxcFfN15/india1.png", 1))
                    imgDao().insert(Img("country12", "https://i.postimg.cc/d1bgv9Jf/india2.png", 2))
                    imgDao().insert(Img("country12", "https://i.postimg.cc/5NqDdvTP/india3.png", 3))

                    GuessDao().insert(Guess("country13","Japón", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country13", "https://i.postimg.cc/zvY24XjZ/japon1.png", 1))
                    imgDao().insert(Img("country13", "https://i.postimg.cc/k5DHbH4Z/japon2.png", 2))
                    imgDao().insert(Img("country13", "https://i.postimg.cc/25JKFxL5/japon3.png", 3))

                    GuessDao().insert(Guess("country14","Andorra", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country14", "https://i.postimg.cc/rsz2Fp4d/ando1.png", 1))
                    imgDao().insert(Img("country14", "https://i.postimg.cc/6qSJ7NVM/ando2.png", 2))
                    imgDao().insert(Img("country14", "https://i.postimg.cc/QNnrYpLc/ando3.png", 3))

                    GuessDao().insert(Guess("country15","Arabia Saudita", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country15", "https://i.postimg.cc/pdBwfjYh/arabia1.png", 1))
                    imgDao().insert(Img("country15", "https://i.postimg.cc/y804NtDT/arabia2.png", 2))
                    imgDao().insert(Img("country15", "https://i.postimg.cc/4dLjMT0X/arabia3.png", 3))

                    GuessDao().insert(Guess("country16","Alemania", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country16", "https://i.postimg.cc/TwtX6bKM/alema1.jpg", 1))
                    imgDao().insert(Img("country16", "https://i.postimg.cc/7LyCCFmP/alema2.png", 2))
                    imgDao().insert(Img("country16", "https://i.postimg.cc/CKHRbcQG/alema3.png", 3))

                    GuessDao().insert(Guess("country17","Italia", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country17", "https://i.postimg.cc/L89wH5Mm/italia1.png", 1))
                    imgDao().insert(Img("country17", "https://i.postimg.cc/3xt6WhKm/italia2.png", 2))
                    imgDao().insert(Img("country17", "https://i.postimg.cc/DZXYCb1P/italia3.png", 3))

                    GuessDao().insert(Guess("country18","Reino Unido", Difficulty.Easy, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country18", "https://i.postimg.cc/gj3Q06J2/inglaterra1.jpg", 1))
                    imgDao().insert(Img("country18", "https://i.postimg.cc/66ZDcF0x/inglaterra2.jpg", 2))
                    imgDao().insert(Img("country18", "https://i.postimg.cc/wTdnGFfy/inglaterra3.jpg", 3))

                    GuessDao().insert(Guess("country19","Australia", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country19", "https://i.postimg.cc/XNPTk4F8/australia1.jpg", 1))
                    imgDao().insert(Img("country19", "https://i.postimg.cc/HLTKkxDp/australia2.jpg", 2))
                    imgDao().insert(Img("country19", "https://i.postimg.cc/3w9q4TY2/australia3.jpg", 3))

                    GuessDao().insert(Guess("country20","Nueva Zelanda", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country20", "https://i.postimg.cc/FsmnZzz0/nueva-Zelan1.jpg", 1))
                    imgDao().insert(Img("country20", "https://i.postimg.cc/Nf4CPt2S/nueva-Zelanda2.jpg", 2))
                    imgDao().insert(Img("country20", "https://i.postimg.cc/7LpKTYjG/nueva-Zelanda3.jpg", 3))

                    GuessDao().insert(Guess("country21","Papúa Nueva Guinea", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country21", "https://i.postimg.cc/sgH6sJcJ/papua1.jpg", 1))
                    imgDao().insert(Img("country21", "https://i.postimg.cc/nLsdzQSf/papua2.jpg", 2))
                    imgDao().insert(Img("country21", "https://i.postimg.cc/gjp4NtzG/papua3.jpg", 3))

                    GuessDao().insert(Guess("country22","Fiyi", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country22", "https://i.postimg.cc/VNNxF67K/fiyi1.jpg", 1))
                    imgDao().insert(Img("country22", "https://i.postimg.cc/qvQfnnGt/fiyi2.jpg", 2))
                    imgDao().insert(Img("country22", "https://i.postimg.cc/SKtw1h3k/fiyi3.jpg", 3))

                    GuessDao().insert(Guess("country23","Islas Salomón", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
                    imgDao().insert(Img("country23", "https://i.postimg.cc/3RmnjQHL/salomon1.jpg", 1))
                    imgDao().insert(Img("country23", "https://i.postimg.cc/RhTd81hK/salomon2.jpg", 2))
                    imgDao().insert(Img("country23", "https://i.postimg.cc/G3PXHRKt/salomon3.jpg", 3))

                }
            }
        }
    }
}