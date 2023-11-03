# Changes over project
- Added a `BetterAiPlayer` to play against the server in the model (separate from `AiPlayer`).
- `AbstractPlayer` had two new fields added to assist with making a better "AI" player.
- `Coord` record obtained JSON property annotations for serialization (inevitable change)
- Driver adjusted to also run the local vs. server game (as required).