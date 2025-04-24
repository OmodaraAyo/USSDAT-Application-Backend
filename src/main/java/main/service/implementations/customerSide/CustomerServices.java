package main.service.implementations.customerSide;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.service.interfaces.custmerSide.CustomerServiceInterface;

public class CustomerServices implements CustomerServiceInterface {
    @Override
    public FetchMenuResponse fetchMenu(FetchMenuRequest request) {
        return null;
    }
}




package main.service.implementations;

import main.dtos.requests.customerFaceRequest.FetchMenuRequest;
import main.dtos.responses.customerFaceResponse.FetchMenuResponse;
import main.dtos.customerFace.CustomerOption;
import main.models.users.Company;
import main.models.users.Option;
import main.repositories.CompanyRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CustomerMenuServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CustomerMenuServiceImpl.class);
    private static final String DEFAULT_CONTEXT = "main";
    private static final int PAGE_SIZE = 5;

    @Autowired
    private CompanyRepo companyRepo;

    @Autowired
    private RedisTemplate<String, FetchMenuResponse> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public FetchMenuResponse fetchMenu(FetchMenuRequest request) {
        String sessionId = request.getSessionId();
        String subCode = request.getSubCode();
        String userResponse = request.getResponse();

        Company company = Optional.ofNullable(companyRepo.findByUssdShortCode(subCode))
                .orElseThrow(() -> new IllegalArgumentException("Invalid company code: " + subCode));

        FetchMenuResponse sessionState = redisTemplate.opsForValue().get(sessionId);
        String currentContext = sessionState != null ? sessionState.getContext() : DEFAULT_CONTEXT;
        int page = sessionState != null ? sessionState.getPage() : 1;

        if (userResponse != null) {
            page = handleUserResponse(sessionState, userResponse);
            currentContext = updateContext(sessionState, userResponse);
        }

        List<CustomerOption> customerOptions = fetchOptions(company, currentContext);
        List<CustomerOption> paginatedOptions = paginateOptions(customerOptions, page);
        boolean hasMore = (page * PAGE_SIZE) < customerOptions.size();

        FetchMenuResponse response = new FetchMenuResponse();
        response.setMessage(formatMenuMessage(company, paginatedOptions, hasMore));
        response.setContext(currentContext);
        response.setHasMore(hasMore);
        response.setOptions(paginatedOptions);
        response.setPage(page);

        redisTemplate.opsForValue().set(sessionId, response, 5, TimeUnit.MINUTES);

        return response;
    }

    private int handleUserResponse(FetchMenuResponse sessionState, String userResponse) {
        if (sessionState != null && "0".equals(userResponse) && sessionState.getHasMore()) {
            return sessionState.getPage() + 1;
        }
        return 1;
    }

    private String updateContext(FetchMenuResponse sessionState, String userResponse) {
        return Optional.ofNullable(sessionState)
                .flatMap(state -> state.getOptions().stream()
                        .filter(opt -> opt.getOption().equals(userResponse))
                        .map(CustomerOption::getContext)
                        .findFirst())
                .orElse(DEFAULT_CONTEXT);
    }

    private List<CustomerOption> fetchOptions(Company company, String currentContext) {
        return "main".equals(currentContext) ?
                company.getMenu().getOptions().stream()
                        .map(opt -> new CustomerOption(String.valueOf(company.getMenu().getOptions().indexOf(opt) + 1),
                                opt.getTitle(), opt.getTitle().toLowerCase().replace(" ", "_")))
                        .collect(Collectors.toList()) :
                fetchSubMenu(company, currentContext);
    }

    private List<CustomerOption> paginateOptions(List<CustomerOption> options, int page) {
        return options.stream()
                .skip((page - 1) * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .collect(Collectors.toList());
    }

    private String formatMenuMessage(Company company, List<CustomerOption> options, boolean hasMore) {
        StringBuilder menuBuilder = new StringBuilder("CON Welcome to ")
                .append(company.getCompanyName()).append("!\nSelect an option:\n");
        options.forEach(opt -> menuBuilder.append(opt.getOption()).append(". ").append(opt.getText()).append("\n"));
        if (hasMore) menuBuilder.append("0. Next\n");
        return menuBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private List<CustomerOption> fetchSubMenu(Company company, String context) {
        try {
            Map<String, Object> response = restTemplate.postForObject(
                    company.getBaseUrl() + "/menu", Map.of("context", context), Map.class);
            return response != null && response.containsKey("options") ?
                    ((List<Map<String, String>>) response.get("options")).stream()
                            .map(opt -> new CustomerOption(opt.get("option"), opt.get("text"), opt.get("context")))
                            .collect(Collectors.toList()) :
                    List.of();
        } catch (Exception e) {
            logger.error("Failed to fetch sub-menu for context {}: {}", context, e.getMessage());
            return List.of();
        }
    }
}