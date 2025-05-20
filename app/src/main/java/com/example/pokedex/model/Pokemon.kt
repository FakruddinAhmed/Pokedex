package com.example.pokedex.model

import com.google.gson.annotations.SerializedName

// For Pokemon list
data class Pokemon(
    val id: Int,
    val name: String,
    val url: String,
    val imageUrl: String = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png",
    val types: List<TypeSlot> = emptyList()
)

data class PokemonResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonResult>
)

data class PokemonResult(
    val name: String,
    val url: String
)

// For Pokemon details
data class PokemonDetails(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: Sprites,
    val types: List<TypeSlot>,
    val stats: List<Stat>,
    val abilities: List<Ability>,
    @SerializedName("base_experience")
    val baseExperience: Int,
    val moves: List<Move>
)

data class Sprites(
    @SerializedName("front_default")
    val frontDefault: String,
    @SerializedName("back_default")
    val backDefault: String?,
    @SerializedName("front_shiny")
    val frontShiny: String?,
    @SerializedName("back_shiny")
    val backShiny: String?
)

data class TypeSlot(
    val type: TypeInfo
)

data class TypeInfo(
    val name: String
)

data class Stat(
    @SerializedName("base_stat")
    val baseStat: Int,
    val effort: Int,
    val stat: StatInfo
)

data class StatInfo(
    val name: String,
    val url: String
)

data class Ability(
    val ability: AbilityInfo,
    @SerializedName("is_hidden")
    val isHidden: Boolean,
    val slot: Int
)

data class AbilityInfo(
    val name: String,
    val url: String
)

data class Move(
    val move: MoveInfo,
    @SerializedName("version_group_details")
    val versionGroupDetails: List<VersionGroupDetail>
)

data class MoveInfo(
    val name: String,
    val url: String
)

data class VersionGroupDetail(
    @SerializedName("level_learned_at")
    val levelLearnedAt: Int,
    @SerializedName("move_learn_method")
    val moveLearnMethod: MoveLearnMethod,
    @SerializedName("version_group")
    val versionGroup: VersionGroup
)

data class MoveLearnMethod(
    val name: String,
    val url: String
)

data class VersionGroup(
    val name: String,
    val url: String
) 