package com.youngpotato.firsttoyprojectback.domain.comment;

import com.youngpotato.firsttoyprojectback.domain.BaseEntity;
import com.youngpotato.firsttoyprojectback.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(schema = "memoji", name = "tb_comment")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "parent_id")
    private Long parentId;
}
