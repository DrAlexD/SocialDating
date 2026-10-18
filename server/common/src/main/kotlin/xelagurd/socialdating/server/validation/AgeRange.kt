package xelagurd.socialdating.server.validation

import java.time.LocalDate
import kotlin.reflect.KClass
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import xelagurd.socialdating.server.utils.DateUtils.toAge

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [AgeRangeValidator::class])
annotation class AgeRange(
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE,
    val message: String = "{socialdating.validation.ageRange}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class AgeRangeValidator : ConstraintValidator<AgeRange, LocalDate> {

    private var min = 0
    private var max = Int.MAX_VALUE

    override fun initialize(constraintAnnotation: AgeRange) {
        min = constraintAnnotation.min
        max = constraintAnnotation.max
    }

    override fun isValid(value: LocalDate?, context: ConstraintValidatorContext?) =
        value == null || value.toAge() in min..max
}
