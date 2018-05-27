<?php
header('Access-Control-Allow-Origin:*');
header('Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept, Connection, User-Agent, Cookie');
//error_reporting(E_ALL);
//ini_set('display_errors', '1');
$method = $_POST['method'];
//getBarData('11700','','author_id');
//getRankData("acute");
//search_term("recovery care");
//$method = 'visual';
//search_both('mazzon e','trauma');
//search_recent();
//var_dump(getBarData('11699','trauma','author_id_term'));
//search_author_id('425','ko wk','Ko Wan-Kyu','Department of Neurosurgery,CHA Bundang Medical Center,CHA University,,Yatap-ro,Bundang-gu,Seongnam-si,Gyeonggi-do,Republic of Korea');
//search_author_id_term('425','acid','Ko WK','Ko Wan-Kyu','Department of Neurosurgery,CHA Bundang Medical Center,CHA University,,Yatap-ro,Bundang-gu,Seongnam-si,Gyeonggi-do,Republic of Korea');
/**
 * assume the user has 4 type of interactions with the website.
 * 1. user does nothing and show the recent 10 articles.
 *    --- link to search_recent function
 * 2. user click on the search button on the top of the website.
 *    --- link to search function
 * 3. when the website shows the list of cards for the authors with the same
 *    name and user click on one of the cards.
 *    --- link to search_author_id function
 * 4. when the website shows the list of cards for the authors with the same
 *    on the same toptic and user click on one of the cards.
 *    --- link to search_author_id_term function
 */
switch($method) {
  case "search_recent":
    search_recent();
    break;
  case "search":
    search();
    break;
  case "search_author_id":
    $author_id = $_POST['author_id'];
    $author_name = $_POST['author_name'];
    $author_fname = $_POST['author_fname'];
    $author_des = $_POST['author_des'];
    search_author_id($author_id,$author_name,$author_fname,$author_des);
    break;
  case "search_author_id_term":
    $author_id = $_POST['author_id'];
    $author_name = $_POST['author_name'];
    $author_fname = $_POST['author_fname'];
    $author_des = $_POST['author_des'];
    $term = $_POST['term'];
    search_author_id_term($author_id,$term,$author_name,$author_fname,$author_des);
    break;
  case "search_term":
    $term = $_POST['term'];
    search_term($term);
    break;
  case "visual":
    getVisualData();
    break;
  case "rank":
    $term = $_POST['term'];
    getRankData($term);
    break;
  default:
    wrong_post();
    break;
}

// function for wrong post
function wrong_post() {
  print_r("wrong post");
}

function getVisualData() {
  $conn = getConnectionDB();
  $response = array();
  $query = 'SELECT * FROM `visual`';
  $result = $conn->query($query);
  $query_year_start = date('Y',time());
  $query_year_end = 2004;
  $response['all'] = array();
  while($query_year_start>=$query_year_end) {
    $response[$query_year_start] = array();
    $query_year_start --;
  }
  while($row = $result->fetch_assoc()) {
    $query_year_start = date('Y',time());
    $query_year_end = 2004;
    $visual_data = array(
      'name'  => $row['term'],
      'value' => $row['all_year'],
      'desc'  => "Total " . $row['all_year'] . " articles"
    );
    array_push($response['all'], $visual_data);
    while($query_year_start>=$query_year_end) {
      $visual_data = array(
        'name'  => $row['term'],
        'value' => $row[$query_year_start],
        'desc'  => "Total " . $row[$query_year_start] . " articles"
      );
      array_push($response[$query_year_start] , $visual_data);
      $query_year_start -- ;
    }
  }
  $response = json_encode($response);
  print $response;
  $conn->close();
}

// function for the request of the recent articles
function search_recent() {
  $conn = getConnectionDB();
//  $query = 'SELECT article_id,article_title,sort_authors,sort_pubdate FROM `articles` ORDER BY sort_pubdate DESC LIMIT 10';
  $query = 'SELECT * FROM `recent`';
  $result = $conn->query($query);
  $data = array();
  while($row = $result->fetch_assoc()){
    $article = array (
      'id' => $row['article_id'],
      'title' => $row['article_title'],
      'author' => $row['sort_authors'],
      'pdate' => $row['sort_pubdate'],
    );
    array_push($data,$article);
  }
  $response['result'] = $data;
  $response['line'] = getBarData("","","all");
  $response1 = json_encode($response);
  print $response1;
  $conn->close();
}


// function for search click
/**
 * assume user will have four type of the interaction with the system when he
 * click the button search on the top of the website
 * 1. the user only enter the keywords for the author input
 *    --- link to search_author function
 * 2. the user only enter the keywords for the term input
 *    --- link to search_term function
 * 3. the user enter both keywords
 *    --- link to search_both function
 * 4. the user enter nothing this action should be blocked from the frontend but
 *    the backend still handle this error for backup.
 */
function search() {
  $term = $_POST['term'];
  $author_search = $_POST['author_search'];
  if ($term == "" & $author_search == "") {
    print "empty search";
    exit();
  } else if($term == ""){
    search_author($author_search);
  } else if($author_search == "") {
    search_term($term);
  } else {
    search_both($author_search,$term);
  }
}

/**
 * this function deals with the issus when the user only search for the author
 * there will be 3 type of situations
 * 1. the author_name is found in the database and there is only one author
 *    called this name. Return the articles wrote by this author
 * 2. the author_name is found in the database but there are several authors
 *    with this name. Return a list of authors with their description for user
 *    to choose the author they want.
 * 3. the author_name is not found in the database. Using fuzzy search Return a
 *    list of possible authors that the user want.
 * @param $begin
 * @param $end
 * @param $author_name
 */
function search_author($author_name) {
  $conn = getConnectionDB();
  $query = "SELECT * FROM `author` WHERE author_name = '".$author_name."' or " .
    "author_fname = '".$author_name."'";
  $result = $conn->query($query);
  $rows = $result->num_rows;
  // find the exact author
  if($rows == 1) {
    $row = $result->fetch_assoc();
    $author_name = $row['author_name'];
    $author_fname = $row['author_fname'];
    $author_des = $row['author_des'];
    $author_id = $row['id'];
    search_author_id($author_id,$author_name,$author_fname,$author_des);
  } else if($rows > 1){  // find the authors with the same name
    $data = array();
    while($row = $result->fetch_assoc()){
      $authors = array (
        'author_id' => $row['id'],
        'author_name' => $row['author_name'],
        'author_fname' => $row['author_fname'],
        'des' => $row['author_des'],
      );
      array_push($data,$authors);
    }
    $response['duplicate'] = $data;
    $response = json_encode($response);
    print $response;
    $conn->close();
  } else {
    $query = "SELECT * FROM `author` WHERE MATCH (author_name,author_fname) AGAINST ('".$author_name."')";
    $result = $conn->query($query);
    $rows = $result->num_rows;
    if($rows > 0) {
      $data = array();
      while($row = $result->fetch_assoc()){
        $authors = array (
          'author_id' => $row['id'],
          'author_name' => $row['author_name'],
          'author_fname' => $row['author_fname'],
          'des' => $row['author_des'],
        );
        array_push($data,$authors);
      }
      $response['duplicate'] = $data;
      $response = json_encode($response);
      print $response;
      $conn->close();
    } else {
      $query = "SELECT * FROM `author` WHERE author_fname REGEXP '" . $author_name . "'";
      $result = $conn->query($query);
      $rows = $result->num_rows;
      if($rows > 0) {
        $data = array();
        while ($row = $result->fetch_assoc()) {
          $authors = array(
            'author_id'    => $row['id'],
            'author_name'  => $row['author_name'],
            'author_fname' => $row['author_fname'],
            'des'          => $row['author_des'],
          );
          array_push($data, $authors);
        }
        $response['duplicate'] = $data;
        $response = json_encode($response);
        print $response;
        $conn->close();
      } else {
        $conn->close();
        $response['result'] = "no result";
        $response = json_encode($response,JSON_UNESCAPED_UNICODE);
        print $response;
      }
    }
  }
}

/**
 * when the user search for both term and author and there will be 3 type of
 * situations
 * 1. the author is found and Return his articles about the term or Return he
 *    wrote nothing about the term
 * 2. several authors are found . Return a list of authors who has wrote about
 *    this term with their description and Return the number of articles they
 *    wrote about this term.
 * 3. no author is found then Return empty
 * @param $begin
 * @param $end
 * @param $author_name
 * @param $term
 */
function search_both($author_name,$term) {
  $conn = getConnectionDB();
  $query = "SELECT * FROM `author` WHERE author_name = '".$author_name."' or " .
    "author_fname = '".$author_name."'";
  $result = $conn->query($query);
  $rows = $result->num_rows;
  // find the exact author
  if($rows == 1) {
    $row = $result->fetch_assoc();
    $author_id = $row['id'];
    $author_name = $row['author_name'];
    $author_fname = $row['author_fname'];
    $author_des = $row['author_des'];
    search_author_id_term($author_id,$term,$author_name,$author_fname,$author_des);
  } else if($rows > 1){  // find the authors with the same name
    $data = array();
    while($row = $result->fetch_assoc()){
      $author_id = $row['id'];
      $author_name = $row['author_name'];
      $author_fname = $row['author_fname'];
      $author_des = $row['author_des'];
      $query_base = getArticleID($author_id);
      $query1 = $query_base ." AND"
        . " MATCH (article_title,abstract,keywords) AGAINST ('". $term
        ."') ";
      $result1 = $conn->query($query1);
      $num =$result1->fetch_assoc();
      $num = $num['num'];
      if($num > 0) {
        $authors = array (
          'author_id' => $row['id'],
          'author_name' => $row['author_name'],
          'author_fname' => $row['author_fname'],
          'des' => $row['author_des'],
          'num_term' => $num,
        );
        array_push($data,$authors);
      }
    }
    if(count($data) === 1){
      search_author_id_term($data[0]['author_id'],$term,$author_name,$data[0]['author_fname'],$data[0]['des']);
      return true;
    }
    $response['duplicate'] = $data;
    $response = json_encode($response,JSON_UNESCAPED_UNICODE);
    print $response;
    $conn->close();
  } else {
    $conn->close();
    $response['result'] = "no result";
    $response = json_encode($response,JSON_UNESCAPED_UNICODE);
    print $response;
  }
}

function search_author_id($author_id,$author_name,$author_fname,$author_des) {
  $response['author'] = array (
    'author_id' => $author_id,
    'author_name' => $author_name,
    'author_fname' => $author_fname,
    'author_des' => $author_des,
  );
  $filter = $_POST['filter'];
  $sort = $_POST['sort'];
  $conn = getConnectionDB();
  $query_base = "SELECT * FROM `authors_articles` AS a JOIN `articles` AS b ON ".
    "a.article_id = b.article_id WHERE author_id = '".$author_id . "' ";
  if($filter === 'all' and $sort === 'relative') {
    $query = $query_base;
  } else if ($filter === 'all' and $sort === 'sort_pubdate') {
    $query = $query_base . "ORDER BY " . $sort . " DESC";
  } else if ($sort === 'sort_pubdate') {
    $query = $query_base . "AND b.year " . $filter . " ORDER BY b." . $sort . " DESC";
  } else {
    $query = $query_base . "AND b.year " . $filter;
  }
  $result = $conn->query($query);
  $rows_num = $result->num_rows;
  if($rows_num > 0) {
    $data = array();
    while($row = $result->fetch_assoc()){
      $article = array (
        'id' => $row['article_id'],
        'title' => $row['article_title'],
        'author' => $row['sort_authors'],
        'pdate' => $row['sort_pubdate'],
      );
      array_push($data,$article);
    }
    $response['result'] = $data;
    $response['bar'] = getBarData($author_id,"","author_id");
    $response['relation'] = getRelation($author_id,$author_name);
    $response['total_num'] = $rows_num;
  } else {
    $response['result'] = "no result";
    $response['bar'] = getBarData($author_id,"","author_id");
    $response['relation'] = getRelation($author_id,$author_name);
  }
  $response = json_encode($response,JSON_UNESCAPED_UNICODE);
  print $response;
  $conn->close();
}

function search_author_id_term($author_id,$term,$author_name,$author_fname,$author_des) {
  $response['author'] = array (
    'author_id' => $author_id,
    'author_name' => $author_name,
    'author_fname' => $author_fname,
    'author_des' => $author_des,
  );
  $filter = $_POST['filter'];
  $sort = $_POST['sort'];
  $conn = getConnectionDB();
  $query = "SELECT article_id FROM authors_articles WHERE author_id = '".$author_id."'";
  $result = $conn->query($query);
  $rows_num = $result->num_rows;
  $data = array();
  if($rows_num>0){
    $row = $result->fetch_assoc();
    $article_list = "article_id = " . $row['article_id'];
    while($row = $result->fetch_assoc()){
      $article_list = $article_list . " OR article_id = " . $row['article_id'];
    }
    $match = "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."')";
    $query_base = "SELECT * ," . $match . " AS relevance " .
             "FROM `articles` WHERE (" . $article_list . ") AND " . $match ;
    if($filter === 'all' and $sort === 'relative') {
      $query = $query_base. " ORDER BY relevance DESC";
    } else if ($filter === 'all' and $sort === 'sort_pubdate') {
      $query = $query_base . " ORDER BY " . $sort . " DESC";
    } else if ($sort === 'sort_pubdate') {
      $query = $query_base . " AND year " . $filter . " ORDER BY b." . $sort . " DESC";
    } else {
      $query = $query_base . " AND year " . $filter . " ORDER BY relevance DESC";
    }
    $result = $conn->query($query);
    $rows_num = $result->num_rows;
    if($rows_num > 0) {
      while($row = $result->fetch_assoc()){
        $article = array (
          'id' => $row['article_id'],
          'title' => $row['article_title'],
          'author' => $row['sort_authors'],
          'pdate' => $row['sort_pubdate'],
        );
        array_push($data,$article);
      }
      $response['result'] = $data;
      $bar_data = getBarData($author_id,$term,"author_id_term");
      $response['relation'] = getRelation($author_id,$author_name);
      $response['bar'] = $bar_data;
      $response['total_num'] = $rows_num;
    } else {
      $response['result'] = "no result";
      if($_POST['method'] !== 'search'){
        $bar_data = getBarData($author_id,$term,"author_id_term");
        $response['relation'] = getRelation($author_id,$author_name);
        $response['bar'] = $bar_data;
      }
    }
  } else {
    $response['result'] = "no result";
    if($_POST['method'] !== 'search'){
      $bar_data = getBarData($author_id,$term,"author_id_term");
      $response['relation'] = getRelation($author_id,$author_name);
      $response['bar'] = $bar_data;
    }
  }
  $response = json_encode($response,JSON_UNESCAPED_UNICODE);
  print $response;
  $conn->close();
}

function search_term($term) {
  $filter = $_POST['filter'];
  $sort = $_POST['sort'];
  $conn = getConnectionDB();
  $title_relevance = "MATCH (article_title) AGAINST ('". $term ."')";
  $abstract_relevance = "MATCH (abstract) AGAINST ('". $term ."')";
  $keywords_relevance = "MATCH (keywords) AGAINST ('". $term ."')";
  $match = "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."')";
  $query_base = "SELECT * ," . $title_relevance . " AS title_relevance,"
    . $abstract_relevance . " AS abstract_relevance ,"
    . $keywords_relevance . " AS keywords_relevance "
    . "FROM `articles` "
    . "WHERE " . $match ;
  if($filter === 'all' and $sort === 'relative') {
    $query = $query_base. " ORDER BY keywords_relevance + title_relevance + "
      . " abstract_relevance DESC";
  } else if ($filter === 'all' and $sort === 'sort_pubdate') {
    $query = $query_base . " ORDER BY " . $sort . " DESC";
  } else if ($sort === 'sort_pubdate') {
    $query = $query_base . " AND year " . $filter . " ORDER BY " . $sort . " DESC";
  } else {
    $query = $query_base . " AND year " . $filter . " ORDER BY keywords_relevance + title_relevance + "
      . " abstract_relevance DESC";
  }
  $result = $conn->query($query);
  $num_rows = $result->num_rows;
  if($num_rows > 0 ){
    $data = array();
    while($row = $result->fetch_assoc()){
      $article = array (
        'id' => $row['article_id'],
        'title' => $row['article_title'],
        'author' => $row['sort_authors'],
        'pdate' => $row['sort_pubdate'],
      );
      array_push($data,$article);
    }
    $response['result'] = $data;
    $response['total_num'] = $num_rows;
    $response['bar'] = getBarData("",$term,"term");
  } else {
    $response['result'] = "no result";
    $response['bar'] = getBarData("",$term,"term");
  }
  $response = json_encode(my_utf8_encode($response),JSON_UNESCAPED_UNICODE);
  print $response;
  $conn->close();
}

function getArticleID($author_id) {
  $conn = getConnectionDB();
  $query =  "SELECT article_id FROM `authors_articles` WHERE author_id = '". $author_id ."'";
  $result = $conn->query($query);
  $row = $result->fetch_assoc();
  $article_list = "article_id = '". $row['article_id'] ."'";
  while ($row = $result->fetch_assoc()) {
    $article_list = $article_list . " OR article_id = " . $row['article_id'];
  }
  $query_base = "SELECT COUNT(*) AS num, year " .
    "FROM `articles` WHERE (" . $article_list . ") " ;
  $conn->close();
  return $query_base;
}

function getBarData($author_id,$term,$method) {
  $data = array();
  $conn = getConnectionDB();
  $query_year_start = date('Y',time());
  $query_year_end = 2004;
  switch($method){
    case "author_id":
      $query_base = getArticleID($author_id);
      $query_base = $query_base . " ";
      break;
    case "author_id_term":
      $match = "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."')";
      $query_base = getArticleID($author_id);
      $query_base = $query_base . " AND " . $match . " " ;
      break;
    case "term":
      $query_base = "SELECT COUNT(*) AS num , * FROM `articles` WHERE "
        . "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."') ";
      break;
    case "all":
      $query_base = "SELECT COUNT(*) AS num , * FROM `articles` ";
      break;
  }
  $query = $query_base . "GROUP BY year ORDER BY year";
  $result = $conn->query($query);
  while($row = $result->fetch_assoc()) {
    while($row['year'] != $query_year_end) {
      $bar = array(
        'key' => $query_year_end,
        'value' => 0
      );
      array_push($data,$bar);
      $query_year_end ++;
    }
    $bar = array(
        'key' => $query_year_end,
        'value' => $row['num'],
      );
    array_push($data,$bar);
    $query_year_end ++;
  }
  while ($query_year_end <= $query_year_start) {
    $bar = array(
      'key' => $query_year_end,
      'value' => 0
    );
    array_push($data,$bar);
    $query_year_end ++;
  }
  return $data;

//  while ($query_year_start >= $query_year_end) {
//    $query = $query_base . " year = " . $query_year_start
//      . " GROUP BY year";
//    $result = $conn->query($query);
//    $row= $result->fetch_assoc();
//    $rows_num = $result->num_rows;
//    if($rows_num > 0 ) {
//      $bar = array(
//        'key' => $query_year_start,
//        'value' => $row['num'],
//      );
//    } else {
//      $bar = array(
//        'key' => $query_year_start,
//        'value' => 0,
//      );
//    }
//    array_push($data,$bar);
//    $query_year_start = $query_year_start - 1;
//  }
//  return $data;
}

function getRankData($term) {
  $data = array();
  $conn = getConnectionDB();
  $match = "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."')";
  $query = "SELECT COUNT(*) AS num , c.author_name, c.id "
    . "FROM `articles` AS a JOIN `authors_articles` AS b JOIN `author` AS c "
    . "ON a.article_id = b.article_id AND b.author_id = c.id "
    . "WHERE " . $match
    . " GROUP BY c.id "
    . "ORDER BY num DESC LIMIT 5";
  $result = $conn->query($query);
  $row = $result->fetch_assoc();
  $response['rank_max'] = $row['num'];
  $article = array (
    'name' => $row['author_name'],
    'value' => $row['num'],
    'author_id' => $row['id'],
    'author_fname' => $row['author_fname'],
    'author_des' => $row['author_des'],
  );
  array_push($data,$article);
  while($row = $result->fetch_assoc()){
    $article = array (
      'name' => $row['author_name'],
      'value' => $row['num'],
      'author_id' => $row['id'],
      'author_fname' => $row['author_fname'],
      'author_des' => $row['author_des'],
    );
    array_push($data,$article);
  }
  $response['rank'] = $data;
  $response['term'] = $term;
  $response = json_encode($response,JSON_UNESCAPED_UNICODE);
  print $response;
  $conn->close();
}

//function getRankMax($term) {
//  $conn = getConnectionDB();
//  $match = "MATCH (article_title,abstract,keywords) AGAINST ('". $term ."')";
//  $query = "SELECT COUNT(*) AS num , c.author_name, c.id "
//    . "FROM `articles` AS a JOIN `authors_articles` AS b JOIN `author` AS c "
//    . "ON a.article_id = b.article_id AND b.author_id = c.id "
//    . "WHERE " . $match
//    . " GROUP BY c.id "
//    . "ORDER BY num DESC LIMIT 1";
//  $result = $conn->query($query);
//  $row = $result->fetch_assoc();
//  return $row['num'];
//}

function getRelation($author_id,$author_name) {
  $conn = getConnectionDB();
  $links = array();
  $nodes = array();
  $node_author = array(
    'name' => $author_name,
    'id' => $author_id,
    'color' => '#D24D25'
  );
  array_push($nodes,$node_author);
  $query = "SELECT *,COUNT(article_id) as num "
    . "FROM `authors_articles` as a join `author` as b on a.author_id = b.id  "
    . "WHERE article_id "
    . "IN (SELECT article_id from `authors_articles` "
    . "WHERE author_id = '" . $author_id. "') "
    . "AND  author_id <> '". $author_id . "' GROUP BY author_id";
  $result = $conn->query($query);
  $i = 1;
  while($row = $result->fetch_assoc()){
    $node = array(
      'name' => $row['author_name'],
      'fname' => $row['author_fname'],
      'des' => $row['author_des'],
      'id' => $row['author_id'],
      'color' => '#FFCD34'
    );
    array_push($nodes,$node);
    $link = array(
      'source' => 0,
      'target' => $i,
      'value' => $row['num']
    );
    array_push($links,$link);
    $i = $i + 1;
  }
  $relation['nodes'] = $nodes;
  $relation['links'] = $links;
  return $relation;
}

function getConnectionDB() {
  $serverName = '127.0.0.1';
  $username = 'root';
  $password = 'root';
  $databaseName = 'spinal_pubmed';
  $conn = new mysqli($serverName,$username,$password);
  $conn->select_db($databaseName);
  return $conn;
}

function my_utf8_encode($data) {
  if (!is_array($data)) {
    return utf8_encode($data);
  } else {
    foreach($data as $key => $val) {
      $data[$key] = my_utf8_encode($val);
    }
  }
  return $data;
}
