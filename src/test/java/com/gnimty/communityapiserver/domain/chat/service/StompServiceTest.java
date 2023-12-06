package com.gnimty.communityapiserver.domain.chat.service;

/**
 * @Slf4j
 * @SpringBootTest
 * @ActiveProfiles(value = "local") public class StompServiceTest {
 * @Autowired private UserRepository userRepository;
 * @Autowired private StompService stompService;
 * @Autowired private UserService userService;
 * @AfterEach void deleteAll() { userRepository.deleteAll(); }
 * @DisplayName("유저의 접속상태 수정")
 * @Nested class updateConnectStatus {
 * @DisplayName("변경한 접속상태로 수정 성공")
 * @Test void successUpdateConnectStatus() { // given User user = User.builder() .actualUserId(1L)
 * .tier(Tier.gold) .division(3) .summonerName("uni") .status(Status.ONLINE) .lp(3L).build();
 * userRepository.save(user);
 * <p>
 * // when stompService.updateConnStatus(user, Status.OFFLINE);
 * <p>
 * // then User findUser = userService.getUser(1L);
 * assertThat(findUser.getStatus()).isEqualTo(Status.OFFLINE); } }
 * <p>
 * }
 **/