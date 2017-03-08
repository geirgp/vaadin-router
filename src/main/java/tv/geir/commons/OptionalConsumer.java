package tv.geir.commons;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OptionalConsumer<T> {
    private Optional<T> optional;

    private OptionalConsumer(Optional<T> optional) {
        this.optional = optional;
    }

    public static <T> OptionalConsumer<T> of(Optional<T> optional) {
        return new OptionalConsumer<>(optional);
    }

    public OptionalConsumer<T> ifPresent(Consumer<T> c) {
        optional.ifPresent(c);
        return this;
    }

    public OptionalConsumer<T> ifNotPresent(Supplier<T> s) {
        if (!optional.isPresent())
            OptionalConsumer.of(Optional.of(s.get()));
        return this;
    }
}