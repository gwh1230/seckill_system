package com.gwh.seckill.vo;

import com.gwh.seckill.utils.ValidatorUtil;
import com.gwh.seckill.validator.IsMobile;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 手机号码校验规则
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (!required && StringUtils.isEmpty(value)) {
            return true;
        }
        return ValidatorUtil.isMobile(value);
    }
}
