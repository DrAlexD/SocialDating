package xelagurd.socialdating.server.validation

import kotlin.reflect.KClass
import org.springframework.beans.BeanWrapperImpl
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext

@MustBeDocumented
@Repeatable
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidRangeValidator::class])
annotation class ValidRange(
    val min: String,
    val max: String,
    val message: String = "{socialdating.validation.range}",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ValidRangeValidator : ConstraintValidator<ValidRange, Any> {

    private lateinit var min: String
    private lateinit var max: String

    override fun initialize(constraintAnnotation: ValidRange) {
        min = constraintAnnotation.min
        max = constraintAnnotation.max
    }

    override fun isValid(value: Any?, context: ConstraintValidatorContext): Boolean {
        val fields = BeanWrapperImpl(value ?: return true)

        @Suppress("UNCHECKED_CAST")
        val minValue = fields.getPropertyValue(min) as Comparable<Any>? ?: return true
        val maxValue = fields.getPropertyValue(max) ?: return true

        if (minValue <= maxValue) return true

        // the error is bound to the max field, so it is described like the errors of the field constraints
        context.disableDefaultConstraintViolation()
        context.unwrap(HibernateConstraintValidatorContext::class.java)
            .addMessageParameter(MIN_NAME_PARAMETER, min.replaceFirstChar { it.titlecase() })
            .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
            .addPropertyNode(max)
            .addConstraintViolation()

        return false
    }

    private companion object {
        const val MIN_NAME_PARAMETER = "minName"
    }
}
