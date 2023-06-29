package com.techbank.cqrs.core.domain;

import com.techbank.cqrs.core.events.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
@NoArgsConstructor
public class AggregateRoot {

    @Getter
    @Setter
    protected String id;

    @Getter
    @Setter
    private long version;

    @Getter
    private final List<BaseEvent> changes = new ArrayList<>();

    private final Logger logger = Logger.getLogger(AggregateRoot.class.getName());

    protected void applyChanges(BaseEvent event, boolean isNewEvent) {
        try {
            var method = getClass().getDeclaredMethod("apply", event.getClass());
            method.setAccessible(true);
            method.invoke(this, event);
        } catch (NoSuchMethodException e) {
            logger.log(Level.WARNING,
                    MessageFormat.format(
                            "the apply method was not found in the aggregate for {0}",
                            event.getClass().getName()
                    )
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error applying event to aggregate", e);
        } finally {
            if(isNewEvent){
                changes.add(event);
            }
        }
    }

    public void markChangesAsCommitted() {
        this.changes.clear();
    }
    public void raiseEvent(BaseEvent event){
        applyChanges(event, true);
    }

    public void replayEvents(Iterable<BaseEvent> events){
        events.forEach(event -> applyChanges(event, false));
    }

}
