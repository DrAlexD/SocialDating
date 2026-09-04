package xelagurd.socialdating.server.validation

import kotlin.reflect.KClass
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [TrimmedSizeValidator::class])
annotation class TrimmedSize(
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
    val message: String = "{socialdating.validation.trimmedSize}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class TrimmedSizeValidator : ConstraintValidator<TrimmedSize, String> {

    private var min = 0
    private var max = Int.MAX_VALUE

    override fun initialize(constraintAnnotation: TrimmedSize) {
        min = constraintAnnotation.min
        max = constraintAnnotation.max
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?) =
        value == null || value.trim().length in min..max
}
