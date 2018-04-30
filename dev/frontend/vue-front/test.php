<?php
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Headers: Origin, X-Requested-With, Content-Type, Accept"); 
header('Access-Control-Allow-Methods: http://localhost:8080/?#/search');
$method = $_POST['method'];
$term = $_POST['term'];
$author_search = $_POST['author'];
$page = $_POST['page'];
$filter = $_POST['filter'];
$sort = $_POST['sort'];


$response = array(
    'term' => 'mysql',
    'author_search' => 'wang',
    'result' => array (
        '1' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '2' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '3' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '4' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '5' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '6' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '7' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '8' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '9' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),
        '10' => array (
            'title' => 'i`m title',
            'author' => 'author1,author2,author3',
            'pdate' => '2018 dec 24',
            'source' => 'book',
            'abstract' => 'dfsdfsfjsldkfjslkdfjsldkfjsdkfjsldfkfjsldkfjsldfkjsldfjslfjskljfskljflsadjfklsadjflsadjflsakfjaldjfalsdfkjasldjfslakjdfslakdfjaslkdfjalsdfjsaldjfasldjfadjfal',
        ),

    ),
    'imagination' => array (
        '1' => 'term1',
        '2' => 'term2',
        '3' => 'term3',
        '4' => 'term4',
        '5' => 'term5',
    ),
    'fuzzy_term' => '####'
);

$res = json_encode(response);
print $res;
print 'dfsdfsdf';
