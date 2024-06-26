package com.dezeta.guessit.domain


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dezeta.guessit.domain.Dao.AnswerTestDao
import com.dezeta.guessit.domain.Dao.ImgDao
import com.dezeta.guessit.domain.Dao.InfoDao
import com.dezeta.guessit.domain.Dao.GuessDao
import com.dezeta.guessit.domain.converter.CategoryConverter
import com.dezeta.guessit.domain.converter.DifficultyConverter
import com.dezeta.guessit.domain.converter.GuessTypeConverter
import com.dezeta.guessit.domain.converter.InstantConverter
import com.dezeta.guessit.domain.entity.AnswerTest
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
    entities = [Img::class, Info::class, Guess::class, AnswerTest::class],
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
    abstract fun AnswerTestDao(): AnswerTestDao


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
                    imgDao().insert(Img("online6", "https://i.postimg.cc/Qts6t8k0/cherno0.jpg", 0))
                    imgDao().insert(Img("online6", "https://i.postimg.cc/cHmFtpFZ/chernobyl1.jpg", 1))
                    imgDao().insert(Img("online6", "https://i.postimg.cc/D0wBrxNG/chernobyl2.jpg", 2))
                    imgDao().insert(Img("online6", "https://i.postimg.cc/44W8fTKQ/chernobyl3.jpg", 3))
                    infoDao().insert(Info("online6", 9.3, SetFecha("2019-08-06"),
                            "Chernóbil es una miniserie de televisión que dramatiza los eventos en torno al desastre nuclear ocurrido en Chernóbil en abril de 1986. La serie se centra en los esfuerzos de limpieza sin precedentes que siguieron al colapso del reactor 4 de la planta nuclear. A través de cinco capítulos, la historia muestra las historias de los bomberos, voluntarios y equipos de mineros que arriesgaron sus vidas para contener la catástrofe. Basada en los recuerdos locales de Prípiat, la serie ofrece una experiencia intensa y conmovedora sobre uno de los eventos más impactantes de la historia moderna."
                        )
                    )

                    GuessDao().insert(Guess("online7","The Crown", Difficulty.Medium, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online7", "https://i.postimg.cc/T3tmmR6t/thecrown0.jpg", 0))
                    imgDao().insert(Img("online7", "https://i.postimg.cc/d0XyH4Dd/theCrown1.png", 1))
                    imgDao().insert(Img("online7", "https://i.postimg.cc/8PDr0Fky/theCrown2.png", 2))
                    imgDao().insert(Img("online7", "https://i.postimg.cc/ry5rjGT0/theCrown3.webp", 3))
                    infoDao().insert(Info("online7", 8.6, SetFecha("2016-11-01"),
                            "The Crown es una serie dramática creada por Peter Morgan que narra la vida de la Reina Isabel II y su reinado desde sus inicios hasta la actualidad. A lo largo de las temporadas, exploramos los desafíos, intrigas y cambios significativos que enfrenta la monarquía británica. La serie ofrece una visión fascinante de la historia y la política detrás del trono, con un reparto excepcional y una cuidada ambientación. Si te interesan las historias de la realeza y la política, The Crown es una elección imperdible."
                        )
                    )

                    GuessDao().insert(Guess("online8","Sons of Anarchy", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online8", "https://i.postimg.cc/QMMQnFmL/sons0.jpg", 0))
                    imgDao().insert(Img("online8", "https://i.postimg.cc/vmSrRG24/sons1.jpg", 1))
                    imgDao().insert(Img("online8", "https://i.postimg.cc/qRh84WWj/sons2.webp", 2))
                    imgDao().insert(Img("online8", "https://i.postimg.cc/Gh1kn7Kx/sons3.png", 3))
                    infoDao().insert(Info("online8", 8.6, SetFecha("2008-09-03"),
                            "Sons of Anarchy es una serie de televisión estadounidense creada por Kurt Sutter. La trama sigue a la pandilla de motociclistas Sons of Anarchy Motorcycle Club, Redwood Original (SAMCRO) en la ficticia ciudad de Charming, California. Liderados por Jax Teller (interpretado por Charlie Hunnam), los miembros de SAMCRO se enfrentan a rivalidades, lealtades divididas y conflictos internos mientras luchan por mantener el control de su territorio y su estilo de vida. La serie combina acción, drama y elementos de tragedia griega, y se desarrolla en un mundo lleno de motos, violencia y secretos oscuros. Si te gustan las historias sobre lealtad, crimen organizado y personajes complejos, Sons of Anarchy es una opción emocionante. "
                        )
                    )

                    GuessDao().insert(Guess("online9","Dexter", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online9", "https://i.postimg.cc/gJNK2v02/dexter0.jpg", 0))
                    imgDao().insert(Img("online9", "https://i.postimg.cc/mDbw4dBn/dester1.jpg", 1))
                    imgDao().insert(Img("online9", "https://i.postimg.cc/PJJQpPLz/dexter2.png", 3))
                    imgDao().insert(Img("online9", "https://i.postimg.cc/J7YqwcgT/dexter3.jpg", 2))
                    infoDao().insert(Info("online9", 8.6, SetFecha("2006-10-01"),
                            "Dexter es una serie de televisión estadounidense que combina elementos de drama, suspenso y crimen. La trama sigue a Dexter Morgan, un forense especializado en análisis de salpicaduras de sangre en el Departamento de Policía de Miami, pero también es un asesino en serie. Dexter tiene un código moral muy particular: solo mata a otros asesinos que han escapado de la justicia. La serie explora su doble vida, sus relaciones personales y su lucha interna entre su lado oscuro y su deseo de ser parte de la sociedad. A lo largo de las temporadas, vemos cómo Dexter enfrenta desafíos, investiga crímenes y mantiene su secreto mientras intenta llevar una vida normal. Si te gustan los personajes complejos y las tramas intrigantes, Dexter es una excelente opción. "
                        )
                    )

                    GuessDao().insert(Guess("online10","The Walking Dead", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online10", "https://i.postimg.cc/rmxKhTBX/walking0.jpg", 0))
                    imgDao().insert(Img("online10", "https://i.postimg.cc/902zKS24/walking1.jpg", 1))
                    imgDao().insert(Img("online10", "https://i.postimg.cc/5tZXqykS/walking2.jpg", 2))
                    imgDao().insert(Img("online10", "https://i.postimg.cc/7hNbkVPK/walking3.png", 3))
                    infoDao().insert(Info("online10", 8.1, SetFecha("2010-10-31"),
                            "The Walking Dead es una serie de televisión estadounidense que combina elementos de drama, suspenso y crimen. La trama sigue a Dexter Morgan, un forense especializado en análisis de salpicaduras de sangre en el Departamento de Policía de Miami, pero también es un asesino en serie. Dexter tiene un código moral muy particular: solo mata a otros asesinos que han escapado de la justicia. La serie explora su doble vida, sus relaciones personales y su lucha interna entre su lado oscuro y su deseo de ser parte de la sociedad. A lo largo de las temporadas, vemos cómo Dexter enfrenta desafíos, investiga crímenes y mantiene su secreto mientras intenta llevar una vida normal. Si te gustan los personajes complejos y las tramas intrigantes, Dexter es una excelente opción."
                        )
                    )


                    GuessDao().insert(Guess("online11","Lost", Difficulty.Easy, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online11", "https://i.postimg.cc/bN217X1J/lost0.png", 0))
                    imgDao().insert(Img("online11", "https://i.postimg.cc/L4WzQ4Yw/lost1.jpg", 1))
                    imgDao().insert(Img("online11", "https://i.postimg.cc/Dz7s48HJ/lost2.png", 2))
                    imgDao().insert(Img("online11", "https://i.postimg.cc/mgYCrQXW/lost3.webp", 3))
                    infoDao().insert(Info("online11", 8.3, SetFecha("2004-09-22"),
                            "Lost es una serie de televisión estadounidense que combina elementos de drama, misterio, ciencia ficción y aventura. La trama sigue a un grupo de sobrevivientes de un accidente de avión que quedan varados en una misteriosa isla tropical. A medida que luchan por sobrevivir, descubren secretos oscuros, criaturas extrañas y una serie de eventos inexplicables. La serie se centra en los personajes, sus relaciones y sus pasados entrelazados, mientras exploran la isla y enfrentan desafíos tanto físicos como emocionales. Lost es conocida por su narrativa no lineal, giros sorprendentes y un final que generó debates entre los fanáticos. Si te gustan las historias intrigantes y llenas de enigmas, esta serie es una excelente opción."
                        )
                    )


                    GuessDao().insert(Guess("online12","Better Call Saul", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online12", "https://i.postimg.cc/4dqQcXpD/saul0.jpg", 0))
                    imgDao().insert(Img("online12", "https://i.postimg.cc/c4qc44xY/saul1.png", 1))
                    imgDao().insert(Img("online12", "https://i.postimg.cc/XY4cdHyY/saul2.jpg", 2))
                    imgDao().insert(Img("online12", "https://i.postimg.cc/4xPv56zb/saul3.jpg", 3))
                    infoDao().insert(Info("online12", 9.0, SetFecha("2015-02-08"),
                            "Better Call Saul es una serie de televisión que sirve como precuela de la exitosa serie “Breaking Bad”. La trama se centra en el personaje del abogado Saul Goodman (interpretado por Bob Odenkirk) seis años antes de su encuentro con Walter White. La historia sigue la transformación de Jimmy McGill, un picapleitos de poca monta con problemas económicos, mientras se convierte en el abogado criminalista Saul Goodman. A medida que Jimmy lucha por sobrevivir en un mundo implacable, descubrimos los motivos y la evolución que lo llevan a adoptar la personalidad extravagante y astuta de Saul Goodman"
                        )
                    )


                    GuessDao().insert(Guess("online13","Dark", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online13", "https://i.postimg.cc/fTk8kgmn/dark0.jpg", 0))
                    imgDao().insert(Img("online13", "https://i.postimg.cc/HsKBM49J/dark1.png", 1))
                    imgDao().insert(Img("online13", "https://i.postimg.cc/nhDTGj33/dark2.png", 2))
                    imgDao().insert(Img("online13", "https://i.postimg.cc/tgGkSqfp/dark3.jpg", 3))
                    infoDao().insert(Info("online13", 8.7, SetFecha("2017-11-20"),
                        "Dark es una serie alemana de ciencia ficción creada por Baran bo Odar y Jantje Friese. La trama se desarrolla en la pequeña ciudad ficticia de Winden, donde cuatro familias están conectadas por secretos oscuros y viajes en el tiempo. A medida que los personajes exploran misterios, paradojas temporales y relaciones complejas, descubrimos que la línea entre pasado, presente y futuro es más delgada de lo que imaginamos. Dark es conocida por su narrativa intrincada, atmósfera sombría y giros sorprendentes. Si te gustan las historias complejas y enigmáticas, esta serie es una excelente elección."
                        )
                    )

                    GuessDao().insert(Guess("online14","La que se avecina", Difficulty.Easy, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online14", "https://i.postimg.cc/3r2fjm7r/avecina0.png", 0))
                    imgDao().insert(Img("online14", "https://i.postimg.cc/KvFq0jvP/avecina1.png", 1))
                    imgDao().insert(Img("online14", "https://i.postimg.cc/bww6j5nd/avecina2.png", 2))
                    imgDao().insert(Img("online14", "https://i.postimg.cc/NjCdGx8D/avecina3.png", 3))
                    infoDao().insert(Info("online14", 7.5, SetFecha("2007-04-22"),
                        "La que se avecina es una serie de comedia española que sigue las peripecias de los variopintos vecinos en el complejo residencial Mirador de Montepinar. En ella, encontrarás situaciones cómicas, enredos amorosos y personajes extravagantes."
                          )
                    )

                    GuessDao().insert(Guess("online15","Aquí no hay quien viva", Difficulty.Easy, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online15", "https://i.postimg.cc/bvMVgRsr/aqui0.png", 0))
                    imgDao().insert(Img("online15", "https://i.postimg.cc/G2y75syQ/aqui1.png", 1))
                    imgDao().insert(Img("online15", "https://i.postimg.cc/T1jkRMQ5/aqui2.png", 2))
                    imgDao().insert(Img("online15", "https://i.postimg.cc/yxCQ4Yhz/aqui3.png", 3))
                    infoDao().insert(Info("online15", 8.2, SetFecha("2003-09-07"),
                        "Aquí no hay quien viva es una serie de televisión española de género humorístico que se emitió en Antena 3 entre el 7 de septiembre de 2003 y el 6 de julio de 2006. La trama gira en torno a la vida de una peculiar comunidad de vecinos en Desengaño 21, un edificio con tres pisos, dos casas por planta, un ático, una portería y un local contiguo."
                         )
                    )

                    GuessDao().insert(Guess("online16","Los Serrano", Difficulty.Easy, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online16", "https://i.postimg.cc/7h4VttDH/serrano0.png", 0))
                    imgDao().insert(Img("online16", "https://i.postimg.cc/tCXkhc2D/serrano1.png", 1))
                    imgDao().insert(Img("online16", "https://i.postimg.cc/7hNNF1Yg/serrano2.png", 2))
                    imgDao().insert(Img("online16", "https://i.postimg.cc/kMNwRCFB/serrano3.png", 3))
                    infoDao().insert(Info("online16", 7.5, SetFecha("2003-04-22"),
                        "Los Serrano es una serie de televisión española de comedia dramática producida por Globomedia. Se emitió originalmente en la cadena española Telecinco y consta de 8 temporadas con un total de 147 episodios. La trama transcurre en el ficticio barrio de Santa Justa, localizado en la Ribera del río Manzanares en Madrid, donde la familia regenta la taberna “Hermanos Serrano”. "
                        )
                    )

                    GuessDao().insert(Guess("online17","Aida", Difficulty.Medium, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online17", "https://i.postimg.cc/FsgqvcnC/aida0.png", 0))
                    imgDao().insert(Img("online17", "https://i.postimg.cc/t4BMZbbh/aida1.png", 1))
                    imgDao().insert(Img("online17", "https://i.postimg.cc/rwVHwmBV/aida2.png", 2))
                    imgDao().insert(Img("online17", "https://i.postimg.cc/43nFp04j/aida3.png", 3))
                    infoDao().insert(Info("online17", 6.6, SetFecha("2005-01-16"),
                        "Aída es una serie de televisión española de comedia de situación creada por Nacho G. Velilla y producida por Globomedia para la cadena Telecinco. La protagonista es Aída, interpretada por Carmen Machi. La trama comienza cuando Aída, tras heredar la casa de su padre, se muda con sus hijos Lorena y Jonathan a vivir con su madre, al tiempo que intenta sacar adelante a su familia."
                        )
                    )

                    GuessDao().insert(Guess("online18","Hospital Central", Difficulty.Medium, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online18", "https://i.postimg.cc/JnN3X3X9/central0.png", 0))
                    imgDao().insert(Img("online18", "https://i.postimg.cc/L8WVF8R0/central1.png", 1))
                    imgDao().insert(Img("online18", "https://i.postimg.cc/vm6tYwZ8/central2.png", 2))
                    imgDao().insert(Img("online18", "https://i.postimg.cc/vBFLD24r/central3.png", 3))
                    infoDao().insert(Info("online18", 5.5, SetFecha("2000-04-30"),
                            "Hospital Central es una serie de televisión española producida por Videomedia para la cadena Telecinco. Se estrenó el 30 de abril de 2000 y finalizó el 27 de diciembre de 2012. La trama gira en torno a las vidas personales y profesionales de los trabajadores del ficticio Hospital Central de Madrid. "
                        )
                    )

                    GuessDao().insert(Guess("online19","Curro Jiménez", Difficulty.Difficult, Category.Spanish ,GuessType.SERIE))
                    imgDao().insert(Img("online19", "https://i.postimg.cc/TY9kFxpZ/curro0.png", 0))
                    imgDao().insert(Img("online19", "https://i.postimg.cc/Dzt5LyMb/curro1.png", 1))
                    imgDao().insert(Img("online19", "https://i.postimg.cc/W3g8nLBY/curro2.png", 2))
                    imgDao().insert(Img("online19", "https://i.postimg.cc/N0ypKrpc/curro3.png", 3))
                    infoDao().insert(Info("online19", 7.3, SetFecha("1976-12-22"),
                        "Curro Jiménez fue una serie de televisión española emitida entre 1976 y 1978 en La 1 de TVE. La trama se basa en el bandolerismo andaluz del siglo XIX, y la acción se desarrolla principalmente en la Serranía de Ronda."
                        )
                    )

                    GuessDao().insert(Guess("online20","The Mentalist", Difficulty.Difficult, Category.Police ,GuessType.SERIE))
                    imgDao().insert(Img("online20", "https://i.postimg.cc/rpg41q2h/mentalist0.jpg", 0))
                    imgDao().insert(Img("online20", "https://i.postimg.cc/3WM08R2w/mentalist1.png", 1))
                    imgDao().insert(Img("online20", "https://i.postimg.cc/7P5BZWgv/mentalist2.png", 2))
                    imgDao().insert(Img("online20", "https://i.postimg.cc/YCH882Tk/mentalist3.png", 3))
                    infoDao().insert(Info("online20", 8.2, SetFecha("2008-09-23"),
                        "The Mentalist es una serie de televisión estadounidense que se emitió desde 2008 hasta 2015. La trama sigue a Patrick Jane, un consultor independiente para el Buró de Investigación de California (CBI), con sede en Sacramento. Aunque no es un oficial de la ley, utiliza sus habilidades de su antigua carrera como un exitoso, aunque admite ser un médium psíquico fraudulento, para ayudar a un equipo de agentes del CBI a resolver asesinatos."
                        )
                    )

                    GuessDao().insert(Guess("online21","The Man in the High Castle", Difficulty.Difficult, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online21", "https://i.postimg.cc/jdjFPjV1/castle0.jpg", 0))
                    imgDao().insert(Img("online21", "https://i.postimg.cc/SKcTk28n/Castle1.png", 1))
                    imgDao().insert(Img("online21", "https://i.postimg.cc/KjpQJnXH/Castle2.png", 2))
                    imgDao().insert(Img("online21", "https://i.postimg.cc/7L1Vpv87/castle3.png", 3))
                    infoDao().insert(Info("online21", 7.9, SetFecha("2015-01-13"),
                        "The Man in the High Castle es una serie de televisión estadounidense que se emitió desde 2015 hasta 2019. La trama se desarrolla en un universo alternativo donde las potencias del Eje (Nazi Alemania y el Imperio de Japón) ganaron la Segunda Guerra Mundial."
                        )
                    )

                    GuessDao().insert(Guess("online22","The Good Doctor", Difficulty.Medium, Category.Medical_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online22", "https://i.postimg.cc/XJHwb42Z/good-Doctor0.png", 0))
                    imgDao().insert(Img("online22", "https://i.postimg.cc/nzcvgC1b/good-Doctor1.png", 1))
                    imgDao().insert(Img("online22", "https://i.postimg.cc/9fBdLGZY/good-Doctor2.jpg", 2))
                    imgDao().insert(Img("online22", "https://i.postimg.cc/5yDLXpWb/gooddoctor3.jpg", 3))
                    infoDao().insert(Info("online22", 7.9, SetFecha("2017-09-25"),
                        ""
                        )
                    )

                    GuessDao().insert(Guess("online23","Reacher", Difficulty.Difficult, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online23", "https://i.postimg.cc/nLV2bRHP/reacher0.jpg", 0))
                    imgDao().insert(Img("online23", "https://i.postimg.cc/nzC1MX9K/reacher1.png", 1))
                    imgDao().insert(Img("online23", "https://i.postimg.cc/ryRJktkM/reacher2.png", 2))
                    imgDao().insert(Img("online23", "https://i.postimg.cc/HnM44F2V/reacher3.png", 3))
                    infoDao().insert(Info("online23", 8.1, SetFecha("2022-09-03"),
                        "Reacher es una serie de televisión basada en los libros de Lee Child, protagonizada por Jack Reacher, un expolicía militar errante que lucha contra criminales peligrosos en sus viajes. La trama se desarrolla en la ciudad de Margrave, Georgia, donde Reacher se ve atrapado en una conspiración letal."
                        )
                    )

                    GuessDao().insert(Guess("online24","Riverdale", Difficulty.Medium, Category.Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online24", "https://i.postimg.cc/9FmyqhzF/riverdale0.png", 0))
                    imgDao().insert(Img("online24", "https://i.postimg.cc/Wb5gGHJm/riverdale1.png", 1))
                    imgDao().insert(Img("online24", "https://i.postimg.cc/bwdb39Q7/riverdale2.png", 2))
                    imgDao().insert(Img("online24", "https://i.postimg.cc/g25h5Lsn/riverdale3.png3", 3))
                    infoDao().insert(Info("online24",   6.5, SetFecha("2017-01-26"),
                        "La serie sigue la vida de un grupo de adolescentes en el pequeño pueblo Riverdale y explora la oscuridad oculta detrás de su imagen aparentemente perfecta."
                        )
                    )

                    GuessDao().insert(Guess("online25","iCarly", Difficulty.Medium, Category.Comedy ,GuessType.SERIE))
                    imgDao().insert(Img("online25", "https://i.postimg.cc/pdwD955g/icarly0.png", 0))
                    imgDao().insert(Img("online25", "https://i.postimg.cc/Twnnn2rh/icarly1.png", 1))
                    imgDao().insert(Img("online25", "https://i.postimg.cc/Rh9KdrRd/icarly2.png", 2))
                    imgDao().insert(Img("online25", "https://i.postimg.cc/DfdqZZvM/icarly3.png", 3))
                    infoDao().insert(Info("online25", 6.8, SetFecha("2007-09-08"),
                        "iCarly es una serie de televisión de comedia de situación adolescente creada por el productor de televisión, Dan Schneider, y transmitida originalmente por la cadena Nickelodeon. La trama sigue la historia de Carly Shay, una adolescente que, junto a sus dos mejores amigos, Sam Puckett y Freddie Benson, crea un webshow llamado iCarly. La serie fue producida por Schneider’s Bakery en asociación con Nickelodeon Productions."
                        )
                    )

                    GuessDao().insert(Guess("online26","Fallout", Difficulty.Medium, Category.Science_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online26", "https://i.postimg.cc/qqXXHBRW/fallout0.png", 0))
                    imgDao().insert(Img("online26", "https://i.postimg.cc/gjDRBMtW/fallaut1.png", 1))
                    imgDao().insert(Img("online26", "https://i.postimg.cc/G3kjyKQz/fallaut2.png", 2))
                    imgDao().insert(Img("online26", "https://i.postimg.cc/pXRJYjcv/fallaut3.png", 3))
                    infoDao().insert(Info("online26", 8.5, SetFecha("2024-04-10"),
                        " Fallout es una serie de televisión postapocalíptica desarrollada por Lisa Joy y Jonathan Nolan para el servicio de vídeo por demanda Amazon Prime Video. La trama nos sitúa 200 años después del apocalipsis nuclear ocurrido en el año 2077. En este mundo devastado, los habitantes de los lujosos refugios se ven obligados a regresar a la superficie y descubren un universo increíblemente complejo, alegremente extraño y muy violento. "
                        )
                    )

                    GuessDao().insert(Guess("online27","Doctor Who", Difficulty.Medium, Category.Criminal_Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online27", "https://i.postimg.cc/qqjhGNgB/who0.png", 0))
                    imgDao().insert(Img("online27", "https://i.postimg.cc/660vKmzs/who1.png", 1))
                    imgDao().insert(Img("online27", "https://i.postimg.cc/k4C23QbX/who2.png", 2))
                    imgDao().insert(Img("online27", "https://i.postimg.cc/k4yBn2QN/who3.png", 3))
                    infoDao().insert(Info("online27", 8.6, SetFecha("2022-09-03"),
                        "Doctor Who es una serie de televisión británica de ciencia ficción producida por la BBC. La trama sigue las aventuras de un Señor del Tiempo conocido como “el Doctor”, quien viaja a través del tiempo y el espacio en su nave espacial, la TARDIS."
                        )
                    )

                    GuessDao().insert(Guess("online28","22.11.63", Difficulty.Difficult, Category.Historial_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online28", "https://i.postimg.cc/05n4zw4m/63-0.jpg", 0))
                    imgDao().insert(Img("online28", "https://i.postimg.cc/26FKFb8s/63-1.png", 1))
                    imgDao().insert(Img("online28", "https://i.postimg.cc/j5r17QK6/63-2.jpg", 2))
                    imgDao().insert(Img("online28", "https://i.postimg.cc/SRStZSnJ/63-3.png", 3))
                    infoDao().insert(Info("online28", 8.1, SetFecha("2016-09-15"),
                        "22.11.63 es una miniserie de televisión basada en la novela 11/22/63 de Stephen King. La trama sigue a Jake Epping (interpretado por James Franco), un profesor de secundaria que, tras su divorcio, decide viajar en el tiempo para impedir el magnicidio del Presidente Kennedy ocurrido el 22 de noviembre de 1963."
                        )
                    )

                    GuessDao().insert(Guess("online29","El señor de los anillos: Los anillos de poder", Difficulty.Medium, Category.Fantasy ,GuessType.SERIE))
                    imgDao().insert(Img("online29", "https://i.postimg.cc/jjLXnrgJ/anillos0.png", 0))
                    imgDao().insert(Img("online29", "https://i.postimg.cc/tT6NDNHq/anillos1.png", 1))
                    imgDao().insert(Img("online29", "https://i.postimg.cc/bvWRQFWV/anillos2.png", 2))
                    imgDao().insert(Img("online29", "https://i.postimg.cc/prtYS52D/anillos3.png", 3))
                    infoDao().insert(Info("online29", 8.1, SetFecha("2022-09-01"),
                        "La serie de televisión ‘El Señor de los Anillos: Los Anillos de Poder’ está ambientada en la Segunda Edad de la Tierra Media y sigue los eventos previos a la trilogía de películas. Los personajes se enfrentan al resurgimiento del mal mientras luchan por la paz y la supervivencia"
                        )
                    )

                    GuessDao().insert(Guess("online30","Imperdonable", Difficulty.Medium, Category.Drama ,GuessType.SERIE))
                    imgDao().insert(Img("online30", "https://i.postimg.cc/hGTX5yh5/imperdonable0.png", 0))
                    imgDao().insert(Img("online30", "https://i.postimg.cc/157VnVLJ/imperdoble1.png", 1))
                    imgDao().insert(Img("online30", "https://i.postimg.cc/6p72d5Kw/imperdoble2.png", 2))
                    imgDao().insert(Img("online30", "https://i.postimg.cc/mg0cVfZ9/imperdoble3.png", 3))
                    infoDao().insert(Info("online30", 7.1, SetFecha("2021-11-24"),
                        "Imperdonable es un drama que sigue la vida de Ruth Slater, una mujer liberada de la cárcel en una sociedad que no está dispuesta a perdonar su pasado. Ruth busca reconectar con su hermana menor, quien tan solo tenía 5 años cuando ocurrió un homicidio que la llevó a prisión. Si tienes más preguntas o necesitas más detalles, no dudes en preguntar. "
                        )
                    )

                    GuessDao().insert(Guess("online31","Resident Evil", Difficulty.Medium, Category.Science_Fiction  ,GuessType.SERIE))
                    imgDao().insert(Img("online31", "https://i.postimg.cc/xdvdRX4r/resident0.png", 0))
                    imgDao().insert(Img("online31", "https://i.postimg.cc/KzQv6dbJ/residen1.png", 1))
                    imgDao().insert(Img("online31", "https://i.postimg.cc/W3s1fsBd/residen2.png", 2))
                    imgDao().insert(Img("online31", "https://i.postimg.cc/C51LSmLY/residen3.png", 3))
                    infoDao().insert(Info("online31", 4.2, SetFecha("2022-07-14"),
                        "La serie sigue a las hermanas Jade y Billie Wesker en New Raccoon City, donde descubren oscuros secretos y un virus que convierte a las personas en zombis. Además, un grupo de policías investiga los sucesos y se enfrenta a la amenaza del virus."
                        )
                    )

                    GuessDao().insert(Guess("online32","Star Trek: Discovery", Difficulty.Medium, Category.Science_Fiction ,GuessType.SERIE))
                    imgDao().insert(Img("online32", "https://i.postimg.cc/85g6k8qG/trek0.png", 0))
                    imgDao().insert(Img("online32", "https://i.postimg.cc/k5bSdMYw/trek1.png", 1))
                    imgDao().insert(Img("online32", "https://i.postimg.cc/8c1fWRpn/trek2.png", 2))
                    imgDao().insert(Img("online32", "https://i.postimg.cc/26dBVcDZ/trek3.png", 3))
                    infoDao().insert(Info("online32", 7.0, SetFecha("2017-09-24"),
                        "Star Trek: Discovery es una serie de televisión estadounidense que sigue las aventuras de la tripulación del USS Discovery mientras exploran nuevos mundos y formas de vida en el espacio. La trama se desarrolla aproximadamente una década antes de la serie original de Star Trek."
                        )
                    )

                    GuessDao().insert(Guess("country1","España", Difficulty.Difficult, Category.NULL ,GuessType.COUNTRY))
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

                    GuessDao().insert(Guess("country15","Arabia Saudí", Difficulty.Medium, Category.NULL ,GuessType.COUNTRY))
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

                    GuessDao().insert(Guess("player1","Lionel Messi", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player1", "https://i.postimg.cc/pXj5sq35/messi1.png", 1))
                    imgDao().insert(Img("player1", "https://i.postimg.cc/7ZyGdSys/messi2.png", 2))
                    imgDao().insert(Img("player1", "https://i.postimg.cc/t4tZj7qX/messi3.png", 3))

                    GuessDao().insert(Guess("player2","Pelé", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player2", "https://i.postimg.cc/6p3TGKCK/pele1.png", 1))
                    imgDao().insert(Img("player2", "https://i.postimg.cc/zvgvPG2Y/pele2.png", 2))
                    imgDao().insert(Img("player2", "https://i.postimg.cc/YjB0cBTG/pele3.png", 3))

                    GuessDao().insert(Guess("player3","Zinedine Zidane", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player3", "https://i.postimg.cc/1tNqVShX/zidane1.png", 1))
                    imgDao().insert(Img("player3", "https://i.postimg.cc/D0kbqNng/zidane2.png", 2))
                    imgDao().insert(Img("player3", "https://i.postimg.cc/kGQRwy5d/zidane3.png", 3))

                    GuessDao().insert(Guess("player4","Ronaldo Nazário", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player4", "https://i.postimg.cc/zvgFNWXd/ronaldo1.png", 1))
                    imgDao().insert(Img("player4", "https://i.postimg.cc/J7Dqjwcb/ronaldo2.png", 2))
                    imgDao().insert(Img("player4", "https://i.postimg.cc/tRKz5r28/ronaldo3.png", 3))

                    GuessDao().insert(Guess("player5","Alfredo Di Stéfano", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player5", "https://i.postimg.cc/vTffks3k/stefano1.png", 1))
                    imgDao().insert(Img("player5", "https://i.postimg.cc/vBp50BL5/stefano2.png", 2))
                    imgDao().insert(Img("player5", "https://i.postimg.cc/h4dLfLcs/stefano3.png", 3))

                    GuessDao().insert(Guess("player6","Paolo Maldini", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player6", "https://i.postimg.cc/pV4nQgct/maldini1.png", 1))
                    imgDao().insert(Img("player6", "https://i.postimg.cc/5yYQd30G/maldini2.png", 2))
                    imgDao().insert(Img("player6", "https://i.postimg.cc/tJKZXxng/maldini3.png", 3))

                    GuessDao().insert(Guess("player7","Ronaldinho", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player7", "https://i.postimg.cc/d1D9WgMc/ronaldinho1.png", 1))
                    imgDao().insert(Img("player7", "https://i.postimg.cc/5ts3SbRm/ronaldinho2.png", 2))
                    imgDao().insert(Img("player7", "https://i.postimg.cc/76cnjXDG/ronaldinho3.png", 3))

                    GuessDao().insert(Guess("player8","Garrincha", Difficulty.Difficult, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player8", "https://i.postimg.cc/G2Mv1tjB/garrincha1.png", 1))
                    imgDao().insert(Img("player8", "https://i.postimg.cc/5y6L9JPs/garrincha2.png", 2))
                    imgDao().insert(Img("player8", "https://i.postimg.cc/mkJMtnJB/garrincha3.png", 3))

                    GuessDao().insert(Guess("player9","Andrés Iniesta", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player9", "https://i.postimg.cc/P5mZ524n/iniesta1.png", 1))
                    imgDao().insert(Img("player9", "https://i.postimg.cc/W1RgVQ07/iniesta2.png", 2))
                    imgDao().insert(Img("player9", "https://i.postimg.cc/d06y83pW/iniesta3.png", 3))

                    GuessDao().insert(Guess("player10","Xavi Hernández", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player10", "https://i.postimg.cc/DwBst5Pv/xavi1.png", 1))
                    imgDao().insert(Img("player10", "https://i.postimg.cc/k5G898Tx/xavi2.png", 2))
                    imgDao().insert(Img("player10", "https://i.postimg.cc/FK6SKR6w/xavi3.png", 3))

                    GuessDao().insert(Guess("player11","Michael Owen", Difficulty.Difficult, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player11", "https://i.postimg.cc/zfxyyMH6/owen.png", 1))
                    imgDao().insert(Img("player11", "https://i.postimg.cc/XJXpd8wJ/owen2.png", 2))
                    imgDao().insert(Img("player11", "https://i.postimg.cc/853sH6Hb/owen3.png", 3))

                    GuessDao().insert(Guess("player12","Fabio Cannavaro", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player12", "https://i.postimg.cc/63BhXDJP/canavaro1.png", 1))
                    imgDao().insert(Img("player12", "https://i.postimg.cc/Y09f4YSf/canavaro2.png", 2))
                    imgDao().insert(Img("player12", "https://i.postimg.cc/qBbxPbmm/canavaro3.png", 3))

                    GuessDao().insert(Guess("player13","Fabio Grosso", Difficulty.Difficult, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player13", "https://i.postimg.cc/0Q573D7R/groso1.png", 1))
                    imgDao().insert(Img("player13", "https://i.postimg.cc/c1bf9KH2/groso2.png", 2))
                    imgDao().insert(Img("player13", "https://i.postimg.cc/gkL8fYLJ/groso3.png", 3))

                    GuessDao().insert(Guess("player14","Francesco Toldo", Difficulty.Difficult, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player14", "https://i.postimg.cc/nhHBRN7S/toldo1.png", 1))
                    imgDao().insert(Img("player14", "https://i.postimg.cc/YqB1gvSH/toldo2.png", 2))
                    imgDao().insert(Img("player14", "https://i.postimg.cc/GhPvjbBs/toldo3.png", 3))

                    GuessDao().insert(Guess("player15","Daniele De Rossi", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player15", "https://i.postimg.cc/65Cf1dPq/rossi1.png", 1))
                    imgDao().insert(Img("player15", "https://i.postimg.cc/bw89XZXT/rossi2.png", 2))
                    imgDao().insert(Img("player15", "https://i.postimg.cc/13HcyysZ/rossi3.png", 3))

                    GuessDao().insert(Guess("player16","Leonardo Bonucci", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player16", "https://i.postimg.cc/W4Ln9d9T/bonucci1.png", 1))
                    imgDao().insert(Img("player16", "https://i.postimg.cc/VNqF2zTM/bonucci2.png", 2))
                    imgDao().insert(Img("player16", "https://i.postimg.cc/9M1YFJcQ/bonucci3.png", 3))

                    GuessDao().insert(Guess("player17","João Cancelo", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player17", "https://i.postimg.cc/CLRsSGXw/cancelo1.png", 1))
                    imgDao().insert(Img("player17", "https://i.postimg.cc/VvV9Xvry/cancelo2.png", 2))
                    imgDao().insert(Img("player17", "https://i.postimg.cc/nh7KQHn5/cancelo3.png", 3))

                    GuessDao().insert(Guess("player18","Andrew Robertson", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player18", "https://i.postimg.cc/8zVt1yvL/roberson1.png", 1))
                    imgDao().insert(Img("player18", "https://i.postimg.cc/zGSp3nw6/roberson2.png", 2))
                    imgDao().insert(Img("player18", "https://i.postimg.cc/jjxcfLZ5/roberson3.png", 3))

                    GuessDao().insert(Guess("player19","Marcos Llorente", Difficulty.Medium, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player19", "https://i.postimg.cc/bJv2L8jq/llorente.png", 1))
                    imgDao().insert(Img("player19", "https://i.postimg.cc/TPfbLg7L/llorente2.png", 2))
                    imgDao().insert(Img("player19", "https://i.postimg.cc/LX0gb7HL/llorente3.png", 3))

                    GuessDao().insert(Guess("player20","Sergio Ramos", Difficulty.Easy, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player20", "https://i.postimg.cc/mZJmnZ2X/ramos1.png", 1))
                    imgDao().insert(Img("player20", "https://i.postimg.cc/MZvstfxf/ramos2.png", 2))
                    imgDao().insert(Img("player20", "https://i.postimg.cc/SN9gb0ny/ramos3.png", 3))

                    GuessDao().insert(Guess("player21","Jadon Sancho", Difficulty.Difficult, Category.NULL ,GuessType.FOOTBALL))
                    imgDao().insert(Img("player21", "https://i.postimg.cc/1z30tb3V/sancho1.png", 1))
                    imgDao().insert(Img("player21", "https://i.postimg.cc/d0N2TnhH/sancho2.png", 2))
                    imgDao().insert(Img("player21", "https://i.postimg.cc/1tgp0669/sancho3.png", 3))

                    GuessDao().insert(Guess("test1","¿En qué película de ciencia ficción se encuentra la inteligencia artificial HAL 9000?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test1", "https://i.postimg.cc/pV52gJMf/test1.png", 0))
                    AnswerTestDao().insert(AnswerTest("test1","2001: Una odisea del espacio",true))
                    AnswerTestDao().insert(AnswerTest("test1","Blade Runner",false))
                    AnswerTestDao().insert(AnswerTest("test1","Matrix",false))
                    AnswerTestDao().insert(AnswerTest("test1","Ex Machina",false))

                    GuessDao().insert(Guess("test2","¿Cuál de las siguientes películas ganó el premio a la Mejor Película en los Premios de la Academia (Oscar) en 2020?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test2", "https://i.postimg.cc/wTK6HR9S/test2.png", 0))
                    AnswerTestDao().insert(AnswerTest("test2","Parasite",true))
                    AnswerTestDao().insert(AnswerTest("test2","Joker",false))
                    AnswerTestDao().insert(AnswerTest("test2","1917",false))
                    AnswerTestDao().insert(AnswerTest("test2","Once Upon a Time in Hollywood",false))

                    GuessDao().insert(Guess("test3","¿Cuál de las siguientes películas ganó el premio a la Mejor Película en los Premios de la Academia (Oscar) en 1994?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test3", "https://i.postimg.cc/bNQqvYJd/test3.png", 0))
                    AnswerTestDao().insert(AnswerTest("test3","Pulp Fiction",false))
                    AnswerTestDao().insert(AnswerTest("test3","Forrest Gump",true))
                    AnswerTestDao().insert(AnswerTest("test3","El silencio de los corderos",false))
                    AnswerTestDao().insert(AnswerTest("test3","Titanic",false))
                    /*
                    genera un tipo test relacionado con alguna pelicula, con 5 preguntas, cada una de ella con 4 respuestas (Dime la correcta). Con un nivel de difficultad medio alto
                     */
                    GuessDao().insert(Guess("test4","¿Cuál de las siguientes películas está basada en una novela de Stephen King?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test4", "https://i.postimg.cc/25YWTpC3/test4.png", 0))
                    AnswerTestDao().insert(AnswerTest("test4","The Sixth Sense",false))
                    AnswerTestDao().insert(AnswerTest("test4","The Shining",true))
                    AnswerTestDao().insert(AnswerTest("test4","The Ring",false))
                    AnswerTestDao().insert(AnswerTest("test4","The Conjuring",false))

                    GuessDao().insert(Guess("test5","¿Qué película de aventuras sigue las hazañas del arqueólogo Indiana Jones en busca de artefactos antiguos?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test5", "https://i.postimg.cc/5tmCRPtg/test5.png", 0))
                    AnswerTestDao().insert(AnswerTest("test5","The Mummy",false))
                    AnswerTestDao().insert(AnswerTest("test5","Raiders of the Lost Ark",true))
                    AnswerTestDao().insert(AnswerTest("test5","National Treasure",false))
                    AnswerTestDao().insert(AnswerTest("test5","Tomb Raider",false))

                    GuessDao().insert(Guess("test6","¿Cuál es el nombre del personaje principal en la película “Forrest Gump”?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test6", "https://i.postimg.cc/xCTzDKJ2/test6.png", 0))
                    AnswerTestDao().insert(AnswerTest("test6","Tom Hanks",false))
                    AnswerTestDao().insert(AnswerTest("test6","Forrest Gump",true))
                    AnswerTestDao().insert(AnswerTest("test6","Lieutenant Dan",false))
                    AnswerTestDao().insert(AnswerTest("test6","Jenny Curran",false))

                    GuessDao().insert(Guess("test7","¿Cuál de las siguientes películas fue dirigida por Christopher Nolan?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test7", "https://i.postimg.cc/SxFYN02V/test7.png", 0))
                    AnswerTestDao().insert(AnswerTest("test7","Primer",false))
                    AnswerTestDao().insert(AnswerTest("test7","Inception (Origen)",true))
                    AnswerTestDao().insert(AnswerTest("test7","Avatar",false))
                    AnswerTestDao().insert(AnswerTest("test7","Titanic",false))

                    GuessDao().insert(Guess("test8","¿Cuántos actores han ganado premios Oscar por interpretar al Joker?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test8", "https://i.postimg.cc/Y9nmphYm/test8.png", 0))
                    AnswerTestDao().insert(AnswerTest("test8","Uno",false))
                    AnswerTestDao().insert(AnswerTest("test8","Dos",true))
                    AnswerTestDao().insert(AnswerTest("test8","Tres",false))
                    AnswerTestDao().insert(AnswerTest("test8","Cuatro",false))

                    GuessDao().insert(Guess("test9","¿En qué película de Quentin Tarantino aparece el personaje Jules Winnfield, interpretado por Samuel L. Jackson?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test9", "https://i.postimg.cc/RhWJq4Lp/test9.png", 0))
                    AnswerTestDao().insert(AnswerTest("test9","Reservoir Dogs",false))
                    AnswerTestDao().insert(AnswerTest("test9","Pulp Fiction",true))
                    AnswerTestDao().insert(AnswerTest("test9","Kill Bill: Volumen 1",false))
                    AnswerTestDao().insert(AnswerTest("test9","Django desencadenado",false))

                    GuessDao().insert(Guess("test10","¿Qué actor interpretó a Neo en la trilogía de “Matrix”?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test10", "https://i.postimg.cc/nrNjDgdF/test10.png", 0))
                    AnswerTestDao().insert(AnswerTest("test10","Laurence Fishburne",false))
                    AnswerTestDao().insert(AnswerTest("test10","Keanu Reeves",true))
                    AnswerTestDao().insert(AnswerTest("test10","Hugo Weaving",false))
                    AnswerTestDao().insert(AnswerTest("test10","Joe Pantoliano",false))

                    GuessDao().insert(Guess("test11","¿Qué nación ha ganado más Copas del Mundo hasta ahora?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test11", "https://i.postimg.cc/L6fBV3jC/test11.png", 0))
                    AnswerTestDao().insert(AnswerTest("test11","Alemania",false))
                    AnswerTestDao().insert(AnswerTest("test11","Brasil",true))
                    AnswerTestDao().insert(AnswerTest("test11","Italia",false))
                    AnswerTestDao().insert(AnswerTest("test11","Argentina",false))

                    GuessDao().insert(Guess("test12","¿Quién es el máximo goleador de la historia de la Copa Mundial de la FIFA?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test12", "https://i.postimg.cc/JzL3YHq1/test12.png", 0))
                    AnswerTestDao().insert(AnswerTest("test12","Pelé",false))
                    AnswerTestDao().insert(AnswerTest("test12","Miroslav Klose",true))
                    AnswerTestDao().insert(AnswerTest("test12","Ronaldo Nazário",false))
                    AnswerTestDao().insert(AnswerTest("test12","Lionel Messi",false))

                    GuessDao().insert(Guess("test13","¿Quién fue el máximo goleador de la Juventus en su ascenso a la Serie A en la 2006/2007?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test13", "https://i.postimg.cc/Qtfsw75v/test13.png", 0))
                    AnswerTestDao().insert(AnswerTest("test13","David Trezeguet",false))
                    AnswerTestDao().insert(AnswerTest("test13","Alessandro Del Piero",true))
                    AnswerTestDao().insert(AnswerTest("test13","Pavel Nedvěd",false))
                    AnswerTestDao().insert(AnswerTest("test13","Gianluigi Buffon",false))

                    GuessDao().insert(Guess("test14","¿Quién es el máximo goleador histórico de la Selección Argentina?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test14", "https://i.postimg.cc/9MmCLb4G/test14.png", 0))
                    AnswerTestDao().insert(AnswerTest("test14","Diego Maradona",false))
                    AnswerTestDao().insert(AnswerTest("test14","Lionel Messi",true))
                    AnswerTestDao().insert(AnswerTest("test14","Gabriel Batistuta",false))
                    AnswerTestDao().insert(AnswerTest("test14","Hernán Crespo",false))

                    GuessDao().insert(Guess("test15","¿Quién es el máximo goleador histórico del FC Bayern de Múnich?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test15", "https://i.postimg.cc/HnjHgL9n/test15.png", 0))
                    AnswerTestDao().insert(AnswerTest("test15","Robert Lewandowski",false))
                    AnswerTestDao().insert(AnswerTest("test15","Gerd Müller",true))
                    AnswerTestDao().insert(AnswerTest("test15","Karl-Heinz Rummenigge",false))
                    AnswerTestDao().insert(AnswerTest("test15","Thomas Müller",false))

                    GuessDao().insert(Guess("test16","¿Cuántos títulos ganó Ronaldo Nazario con el Real Madrid?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test16", "https://i.postimg.cc/5yGJNMs3/test16.png", 0))
                    AnswerTestDao().insert(AnswerTest("test16","1",false))
                    AnswerTestDao().insert(AnswerTest("test16","2",true))
                    AnswerTestDao().insert(AnswerTest("test16","3",false))
                    AnswerTestDao().insert(AnswerTest("test16","4",false))

                    GuessDao().insert(Guess("test17","¿Cuál es el nombre del río más largo del mundo?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test17", "https://i.postimg.cc/pV1RBDPF/test17.png", 0))
                    AnswerTestDao().insert(AnswerTest("test17","Nilo",true))
                    AnswerTestDao().insert(AnswerTest("test17","Amazonas",false))
                    AnswerTestDao().insert(AnswerTest("test17","Danubio",false))
                    AnswerTestDao().insert(AnswerTest("test17","Ebro",false))

                    GuessDao().insert(Guess("test18","¿Cuál es el océano más grande del mundo?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test18", "https://i.postimg.cc/02MxXbNH/test18.png", 0))
                    AnswerTestDao().insert(AnswerTest("test18","Océano Índico",false))
                    AnswerTestDao().insert(AnswerTest("test18","Océano Pacífico",true))
                    AnswerTestDao().insert(AnswerTest("test18","Océano Atlántico",false))
                    AnswerTestDao().insert(AnswerTest("test18","Océano Glacial Antártico",false))

                    GuessDao().insert(Guess("test19","¿Cuál es el país más grande del mundo?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test19", "https://i.postimg.cc/g23pdQ8V/test19.png", 0))
                    AnswerTestDao().insert(AnswerTest("test19","China",false))
                    AnswerTestDao().insert(AnswerTest("test19","Rusia",true))
                    AnswerTestDao().insert(AnswerTest("test19","India",false))
                    AnswerTestDao().insert(AnswerTest("test19","Andorra",false))

                    GuessDao().insert(Guess("test20","¿Cuál es el país que tiene forma de bota?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test20", "https://i.postimg.cc/L6cSc2Y4/test20.png", 0))
                    AnswerTestDao().insert(AnswerTest("test20","Honduras",false))
                    AnswerTestDao().insert(AnswerTest("test20","Italia",true))
                    AnswerTestDao().insert(AnswerTest("test20","España",false))
                    AnswerTestDao().insert(AnswerTest("test20","Dinamarca",false))

                    GuessDao().insert(Guess("test21","¿Cuál es el país más poblado de la tierra?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test21", "https://i.postimg.cc/k5cdRSjD/test21.png", 0))
                    AnswerTestDao().insert(AnswerTest("test21","Rusia",false))
                    AnswerTestDao().insert(AnswerTest("test21","India",true))
                    AnswerTestDao().insert(AnswerTest("test21","Estados Unidos",false))
                    AnswerTestDao().insert(AnswerTest("test21","China",false))

                    GuessDao().insert(Guess("test22","¿Cuál es la capital de Venezuela?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test22", "https://i.postimg.cc/yNbHsZvR/test22.png", 0))
                    AnswerTestDao().insert(AnswerTest("test22","Bogotá",false))
                    AnswerTestDao().insert(AnswerTest("test22","Caracas",true))
                    AnswerTestDao().insert(AnswerTest("test22","Lima",false))
                    AnswerTestDao().insert(AnswerTest("test22","Quito",false))

                    GuessDao().insert(Guess("test23","¿Cuál es la capital de Dinamarca?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test23", "https://i.postimg.cc/gj9b6P9k/test23.png", 0))
                    AnswerTestDao().insert(AnswerTest("test23","Oslo",false))
                    AnswerTestDao().insert(AnswerTest("test23","Copenhague",true))
                    AnswerTestDao().insert(AnswerTest("test23","Bratislava",false))
                    AnswerTestDao().insert(AnswerTest("test23","Liubliana",false))

                    GuessDao().insert(Guess("test24","¿Cuál es la capital de Egipto?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test24", "https://i.postimg.cc/YS9H53fk/test24.png", 0))
                    AnswerTestDao().insert(AnswerTest("test24","Ankara",false))
                    AnswerTestDao().insert(AnswerTest("test24","El Cairo",true))
                    AnswerTestDao().insert(AnswerTest("test24","Atenas",false))
                    AnswerTestDao().insert(AnswerTest("test24","Asmara",false))

                    GuessDao().insert(Guess("test25","¿Cuál es la capital de Bélgica?", Difficulty.Easy, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test25", "https://i.postimg.cc/DZ4KZwMM/test25.png", 0))
                    AnswerTestDao().insert(AnswerTest("test25","Berlín",false))
                    AnswerTestDao().insert(AnswerTest("test25","Bruselas",true))
                    AnswerTestDao().insert(AnswerTest("test25","Berna",false))
                    AnswerTestDao().insert(AnswerTest("test25","Ámsterdam",false))

                    GuessDao().insert(Guess("test26","¿Cuál es la capital de India?", Difficulty.Difficult, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test26", "https://i.postimg.cc/ZY3Sz039/test26.png", 0))
                    AnswerTestDao().insert(AnswerTest("test26","Islamabad",false))
                    AnswerTestDao().insert(AnswerTest("test26","Nueva Deli",true))
                    AnswerTestDao().insert(AnswerTest("test26","Pekín",false))
                    AnswerTestDao().insert(AnswerTest("test26","Bangkok",false))

                    GuessDao().insert(Guess("test27","¿Cuál es la capital de Corea del Sur?", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test27", "https://i.postimg.cc/VvjQqH19/test27.png", 0))
                    AnswerTestDao().insert(AnswerTest("test27","Tokio",false))
                    AnswerTestDao().insert(AnswerTest("test27","Seúl",true))
                    AnswerTestDao().insert(AnswerTest("test27","Pekín",false))
                    AnswerTestDao().insert(AnswerTest("test27","Castellar de la Muela",false))

                   /* GuessDao().insert(Guess("test","", Difficulty.Medium, Category.NULL ,GuessType.TEST))
                    imgDao().insert(Img("test", "", 0))
                    AnswerTestDao().insert(AnswerTest("test","",false))
                    AnswerTestDao().insert(AnswerTest("test","",true))
                    AnswerTestDao().insert(AnswerTest("test","",false))
                    AnswerTestDao().insert(AnswerTest("test","",false))*/
                }
            }
        }
    }
}