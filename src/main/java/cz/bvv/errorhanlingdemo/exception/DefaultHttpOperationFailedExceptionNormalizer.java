package cz.bvv.errorhanlingdemo.exception;

import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.stereotype.Component;

@Component
public class DefaultHttpOperationFailedExceptionNormalizer
  implements ExceptionNormalizer<HttpOperationFailedException> {
}
