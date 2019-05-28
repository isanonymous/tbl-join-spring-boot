package zlink.examples.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import zlink.examples.entity.Emp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import zlink.anno.FromTbl;
import zlink.anno.JoinAndSelect;

import java.util.List;

public interface ForTestEmpMapper extends BaseMapper<Emp> {
  @FromTbl("emp.name, emp.dept ed")
  @JoinAndSelect(freeJoinType = "inner join",value = "dept.name dn, dept.sheng")
  @JoinAndSelect(order = 2,freeJoinType = "inner join",value = "sheng_name")
    //select emp.name, emp.dept ed,dept.name dn, dept.sheng,sheng_name 
    // from emp 
    // inner join Dept on Emp.dept=Dept.id 
    // inner join sheng on Dept.sheng=sheng.shengId 
  List<Emp> empDeptSheng();  //上面的注释为最后生成的sql

  /**
   * 分页和条件查询
   * @Autowired
   * ForTestEmpMapper forTestEmpMapper;
   * List<Emp> empList = forTestEmpMapper.list(
   *    new Page<>(1,99),
   *    new QueryWrapper<Emp>().eq("name","张三"));
   */
  @FromTbl("emp.name, emp.dept ed")
  @JoinAndSelect(freeJoinType = "inner join",value = "dept.name dn, dept.sheng")
  @JoinAndSelect(order = 2,freeJoinType = "inner join",value = "sheng_name")
  List<Emp> empDeptShengPage(IPage<Emp> page, @Param(Constants.WRAPPER) Wrapper<Emp> queryWrapper);
}
