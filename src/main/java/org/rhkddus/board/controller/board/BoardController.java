package org.rhkddus.board.controller.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.rhkddus.board.dto.board.BoardDTO;
import org.rhkddus.board.dto.PageRequestDTO;
import org.rhkddus.board.security.dto.AuthMemberDTO;
import org.rhkddus.board.service.board.BoardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequestMapping("/board/")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;


    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        log.info("list........"+ pageRequestDTO);

        model.addAttribute("result", boardService.getList(pageRequestDTO));

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/register")
    public void register(@AuthenticationPrincipal AuthMemberDTO authMemberDTO, Model model){

        log.info("board register");

        log.info(authMemberDTO);

        model.addAttribute("email", authMemberDTO.getEmail());

    }

    @PostMapping("/register")
    public String registerPost(BoardDTO dto, RedirectAttributes redirectAttributes){

        log.info("dto...."+ dto);

        Long bno = boardService.register(dto);

        log.info("BNO : " + bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";



    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read","/modify"})
    public void read(@ModelAttribute("requestDTO") PageRequestDTO pageRequestDTO,
                     @AuthenticationPrincipal AuthMemberDTO authMemberDTO, Long bno, Model model){

        log.info("bno:" + bno);

        BoardDTO boardDTO = boardService.get(bno);

        log.info(boardDTO);

        model.addAttribute("email",authMemberDTO.getEmail());
        model.addAttribute("dto", boardDTO);

    }

    @PostMapping("/remove")
    public String remove(long bno, RedirectAttributes redirectAttributes){

        log.info("bno: " + bno);

        boardService.removeWithReplies(bno);

        redirectAttributes.addFlashAttribute("msg", bno);

        return "redirect:/board/list";


    }

    @PostMapping("/modify")
    public String modify(BoardDTO dto,@ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes){

        log.info("POST Modify");
        log.info("dto : " + dto);

        boardService.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());
        redirectAttributes.addAttribute("bno", dto.getBno());

        return "redirect:/board/read";

    }


}
