package com.sparta.myselectshop.repository;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByUser(User user, Pageable pageable);

    Page<Product> findAllByUserAndProductFolderList_FolderId(User user, Long folderId, Pageable pageable);
/*
select
    p.id,
    p.title as product_title,
    pf.product_id as product_id,
    pf.folder_id as folder_id
from
    product p left join  product_folder pf
        on p.id = pf.product_id
where p.user_id = 1
and
    pf.folder_id = 3;
 */

}
