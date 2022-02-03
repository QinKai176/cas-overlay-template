package com.demo.handler;

import com.demo.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.services.UnauthorizedServiceForPrincipalException;
import org.apereo.cas.ticket.AbstractTicketException;
import org.apereo.cas.web.flow.CasWebflowConstants;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.action.EventFactorySupport;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class MyAuthenticationExceptionHandlerAction extends AbstractAction {

    private static final String DEFAULT_MESSAGE_BUNDLE_PREFIX = "authenticationFailure.";
    private static final String UNKNOWN = "UNKNOWN";

    /**
     * Ordered list of error classes that this class knows how to handle.
     */
    private final Set<Class<? extends Throwable>> errors;

    private String messageBundlePrefix = DEFAULT_MESSAGE_BUNDLE_PREFIX;

    public MyAuthenticationExceptionHandlerAction(final Set<Class<? extends Throwable>> errors) {
        this.errors = errors;
    }

    @Override
    protected Event doExecute(final RequestContext requestContext) {
        final Event currentEvent = requestContext.getCurrentEvent();
        final Exception error = currentEvent.getAttributes().get(CasWebflowConstants.TRANSITION_ID_ERROR, Exception.class);
        if (error != null) {
            final String event = handle(error, requestContext);
            return new EventFactorySupport().event(this, event, currentEvent.getAttributes());
        }
        return new EventFactorySupport().event(this, "error");
    }

    private String handle(final Exception e, final RequestContext requestContext) {
        final MessageContext messageContext = requestContext.getMessageContext();

        if (e instanceof AuthenticationException) {
            return handleAuthenticationException((AuthenticationException) e, requestContext);
        }

        if (e instanceof AbstractTicketException) {
            return handleAbstractTicketException((AbstractTicketException) e, requestContext);
        }

        final String messageCode = this.messageBundlePrefix + UNKNOWN;
        messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
        return UNKNOWN;
    }

    private String handleAuthenticationException(final AuthenticationException e, final RequestContext requestContext) {
        if (e.getHandlerErrors().containsKey(UnauthorizedServiceForPrincipalException.class.getSimpleName())) {
            final URI url = requestContext.getFlowScope().get("unauthorizedRedirectUrl", URI.class);
            if (url != null) {
                return CasWebflowConstants.STATE_ID_SERVICE_UNAUTHZ_CHECK;
            }
        }

        final Collection<Class> values = e.getHandlerErrors().values().stream().map(Throwable::getClass).collect(Collectors.toList());
        final String handlerErrorName = this.errors
                .stream()
                .filter(values::contains)
                .map(Class::getSimpleName)
                .findFirst()
                .orElseGet(() -> {
                    return UNKNOWN;
                });
        final MessageContext messageContext = requestContext.getMessageContext();
        final String messageCode = this.messageBundlePrefix + handlerErrorName;
        if (Objects.equals(handlerErrorName, "MyException")) {
            MyException ex = (MyException) e.getHandlerErrors().get("MyAuthenticationHandler");
            messageContext.addMessage(new MessageBuilder().error().code(messageCode).args(ex.getMsg()).build());
        } else {
            messageContext.addMessage(new MessageBuilder().error().code(messageCode).build());
        }
        return handlerErrorName;
    }

    private String handleAbstractTicketException(final AbstractTicketException e, final RequestContext requestContext) {
        final MessageContext messageContext = requestContext.getMessageContext();
        errors.add(MyException.class);
        final Optional<String> match = this.errors.stream()
                .filter(c -> c.isInstance(e)).map(Class::getSimpleName)
                .findFirst();
        match.ifPresent(s -> messageContext.addMessage(new MessageBuilder().error().code(e.getCode()).build()));
        return match.orElse(UNKNOWN);
    }

}
