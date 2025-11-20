package biz.util;

import biz.login.vo.LoginVO;
import biz.menu.service.MenuService;
import biz.menu.vo.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 모든 컨트롤러에 공통적으로 적용되는 설정
 * 메뉴 목록을 전역으로 제공하여 권한별 동적 메뉴 구성
 */
@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @Resource(name = "MenuService")
    private MenuService menuService;




    /**
     * 모든 뷰에 메뉴 목록을 자동으로 추가
     * 로그인한 사용자의 권한에 따라 메뉴 목록을 동적으로 제공
     *
     * @return 권한별 메뉴 목록
     */
//    @ModelAttribute("menuList")
//    public List<MenuVO> populateMenu() {
//        LoginVO loginVO = SessionUtil.getLoginUser();
//
//        // 로그인하지 않은 경우 빈 리스트 반환
//        if (loginVO == null) {
//            return List.of();
//        }
//
//        // 사용자 권한 코드 조회
//        String roleCd = loginVO.getRoleCd();
//
//        if (roleCd == null || roleCd.isEmpty()) {
//            return List.of();
//        }
//
//        // 권한별 메뉴 목록 조회
//        List<MenuVO> menuList = menuService.getMenuListByRole(roleCd);
//
//        return menuList;
//    }
    @ModelAttribute("menuList")
    public List<MenuVO> populateMenu() {
        LoginVO loginVO = SessionUtil.getLoginUser();
        if (loginVO == null) return java.util.Collections.emptyList();

        // ── 하드코딩 메뉴 트리 ─────────────────────────────────────────
        // 1차: 대시보드 / 사용자관리(하위2) / 출입관리 / 장비관리 / 정산관리
        MenuVO dashboard = vo("대시보드", "/main/mainForm.do", "dashboard");

        // 사용자관리 하위: 계정관리 / 비밀번호초기화
        MenuVO account   = vo("계정관리",       "/user/manage.do", "user");
        MenuVO accountCreate   = vo("계정등록",       "/user/create.do", "user");
        //MenuVO pwReset   = vo("비밀번호초기화", "/system/user/password/reset.do", "key");
        MenuVO userMgmt  = voWithSubs("사용자관리", "/system/user", "users",
                List.of(account,accountCreate));

        MenuVO access    = vo("출입관리", "/enter/enter.do",            "door");
        MenuVO equip     = vo("장비관리", "/equipment",         "monitor");
        MenuVO settle    = vo("정산관리", "/settlement",        "wallet");

        return Arrays.asList(dashboard, userMgmt, access, equip, settle);
        // ─────────────────────────────────────────────────────────────
    }

    // === 헬퍼 ===
    private static MenuVO vo(String name, String url, String icon) {
        MenuVO m = new MenuVO();
        m.setMenuNm(name);
        m.setMenuUrl(url);
        m.setMenuIcon(icon);
        return m;
    }

    private static MenuVO voWithSubs(String name, String url, String icon, List<MenuVO> subs) {
        MenuVO m = vo(name, url, icon);
        m.setSubMenuList(subs);
        return m;
    }
}

