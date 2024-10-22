package bot

open class AiException(message: String) : Exception(message)
// TODO: Catch Exceptions
class AiWrongFormatException(message: String) : AiException(message)
class AiInvalidOutputException(message: String) : AiException(message)
class AiNoAnswerException(message: String = "Did not get an answer from AI.") : AiException(message)
