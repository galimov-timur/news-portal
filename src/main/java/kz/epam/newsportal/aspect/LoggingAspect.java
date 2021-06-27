package kz.epam.newsportal.aspect;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;

@Aspect
public class LoggingAspect {
    private final Logger LOGGER =Logger.getLogger(getClass().getName());

    private static final String REPOSITORY_POINTCUT = "execution(* kz.epam.newsportal.repository.*.*(..))";
    private static final String REPOSITORY_LOG_MSG = "Repository: calling method ";

    @Pointcut(REPOSITORY_POINTCUT)
    private void repositoryLogging() {}

    @After("repositoryLogging()")
    public void after(JoinPoint theJointPoint) {
        String theMethod = theJointPoint.getSignature().toShortString();
        LOGGER.info(REPOSITORY_LOG_MSG + theMethod);
    }
}
