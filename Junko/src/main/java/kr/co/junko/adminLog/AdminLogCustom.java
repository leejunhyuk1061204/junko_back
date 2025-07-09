package kr.co.junko.adminLog;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AdminLogCustom {
	
	String value(); // ex) "상품 등록", "상품 삭제"
	
}
