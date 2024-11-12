package ru.nsu.ostest.adapter.in.rest.config;

import jakarta.persistence.criteria.Path;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.nsu.ostest.adapter.out.persistence.entity.group.Group;
import ru.nsu.ostest.adapter.out.persistence.entity.user.User;
import ru.nsu.ostest.domain.service.*;

@Configuration
public class FilterConfig {

    @Bean
    public PathResolver<User> userPathResolver() {
        return (root, fieldName) -> {
            if ("groupName".equals(fieldName)) {
                return root.join("groups").get("groupName");
            } else if ("roleName".equals(fieldName)) {
                return root.join("roles").get("role").get("roleName");
            }
            return root.get(fieldName);
        };
    }

    @Bean
    public PathResolver<Group> groupPathResolver() {
        return Path::get;
    }

    @Bean
    @Qualifier("userFilterStrategyFactory")
    @Primary
    public FilterStrategyFactory<User> userFilterStrategyFactory(PathResolver<User> userPathResolver) {
        FilterStrategyFactory<User> factory = new FilterStrategyFactory<>();
        factory.registerStrategy("string", new StringFilterStrategy<>(userPathResolver));
        factory.registerStrategy("integer", new IntegerFilterStrategy<>(userPathResolver));
        return factory;
    }

    @Bean
    @Qualifier("userFilterService")
    public FilterService<User> userFilterService(@Qualifier("userFilterStrategyFactory") FilterStrategyFactory<User> userFilterStrategyFactory) {
        return new FilterServiceImpl<>(userFilterStrategyFactory);
    }

    @Bean
    @Qualifier("groupFilterStrategyFactory")
    public FilterStrategyFactory<Group> groupFilterStrategyFactory(PathResolver<Group> groupPathResolver) {
        FilterStrategyFactory<Group> factory = new FilterStrategyFactory<>();
        factory.registerStrategy("string", new StringFilterStrategy<>(groupPathResolver));
        factory.registerStrategy("integer", new IntegerFilterStrategy<>(groupPathResolver));
        return factory;
    }

    @Bean
    @Qualifier("groupFilterService")
    public FilterService<Group> groupFilterService(@Qualifier("groupFilterStrategyFactory") FilterStrategyFactory<Group> groupFilterStrategyFactory) {
        return new FilterServiceImpl<>(groupFilterStrategyFactory);
    }
}
