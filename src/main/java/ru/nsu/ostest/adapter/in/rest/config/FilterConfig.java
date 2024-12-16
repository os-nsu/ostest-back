package ru.nsu.ostest.adapter.in.rest.config;

import jakarta.persistence.criteria.JoinType;
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
                return root.join("groups", JoinType.LEFT).get("groupName");
            } else if ("roleName".equals(fieldName)) {
                return root.join("roles", JoinType.LEFT).get("role").get("roleName");
            }
            return root.get(fieldName);
        };
    }

    @Bean
    public PathResolver<Group> groupPathResolver() {
        return Path::get;
    }

    @Bean
    @Qualifier(BeanNamesConfig.USER_FILTER_STRATEGY_PROVIDER)
    @Primary
    public FilterStrategyProvider<User> userFilterStrategyProvider(PathResolver<User> userPathResolver) {
        FilterStrategyProvider<User> factory = new FilterStrategyProvider<>();
        factory.registerStrategy("string", new StringFilterStrategy<>(userPathResolver));
        factory.registerStrategy("integer", new IntegerFilterStrategy<>(userPathResolver));
        return factory;
    }

    @Bean
    @Qualifier(BeanNamesConfig.USER_FILTER_SERVICE)
    public FilterService<User> userFilterService(@Qualifier(BeanNamesConfig.USER_FILTER_STRATEGY_PROVIDER) FilterStrategyProvider<User> userFilterStrategyProvider) {
        return new FilterServiceImpl<>(userFilterStrategyProvider);
    }

    @Bean
    @Qualifier(BeanNamesConfig.GROUP_FILTER_STRATEGY_PROVIDER)
    public FilterStrategyProvider<Group> groupFilterStrategyProvider(PathResolver<Group> groupPathResolver) {
        FilterStrategyProvider<Group> factory = new FilterStrategyProvider<>();
        factory.registerStrategy("string", new StringFilterStrategy<>(groupPathResolver));
        factory.registerStrategy("integer", new IntegerFilterStrategy<>(groupPathResolver));
        return factory;
    }

    @Bean
    @Qualifier(BeanNamesConfig.GROUP_FILTER_SERVICE)
    public FilterService<Group> groupFilterService(@Qualifier(BeanNamesConfig.GROUP_FILTER_STRATEGY_PROVIDER) FilterStrategyProvider<Group> groupFilterStrategyProvider) {
        return new FilterServiceImpl<>(groupFilterStrategyProvider);
    }

    @Bean
    public GroupMetaProvider groupMetaProvider() {
        return new GroupMetaProvider();
    }

    @Bean
    public UserMetaProvider userMetaProvider() {
        return new UserMetaProvider();
    }
}
