package com.example.gameon.classes

data class Preferences(
    val preference_id: String? = null,
    val spoken_language: String,
    val time_zone: String,
    val skill_level: String,
    val discord_id: String? = null,
    val user: User? = null,
    val game_id: Int? = null,
    val game: Game? = null
) {
    init {
        require( discord_id != null || user != null ) {
            "Either discord_id or user must not be null"
        }
        require( game_id != null || game != null ) {
            "Either game_id or game must not be null"
        }
    }
}
