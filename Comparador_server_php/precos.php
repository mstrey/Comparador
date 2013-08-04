<?PHP
	
include_once 'connect.php';

	$codBarras = $_GET["produto"];
	$preco = $_GET["preco"];
	$listaProdutos = $_GET["listaProdutos"];
	$loja_atual = $_GET["loja_atual"];
	$lojas_comparar = $_GET["lojas_comparar"];
	$saida = '';
	
	if ($codBarras) {
		
		if ($lojas_comparar) {
			$query =  "	SELECT pr.produto_id produto_id, ";
			$query .= " 	   pd.descricao descricao, ";
			$query .= " 	   pr.loja_id loja_id, ";
			$query .= " 	   pr.preco preco, ";
			$query .= " 	   pr.dt_confirmacao dt_confirmacao ";
			$query .= " FROM produtos pd, ";
			$query .= " 	 precos pr ";
			$query .= " WHERE pr.produto_id = pd.produto_id ";
			$query .= " AND pr.produto_id = $codBarras ";
			$query .= " AND pr.loja_id in ($lojas_comparar);";
		
			$resultSet = mysql_query($query);
		
			while($precos = mysql_fetch_array($resultSet)){
				
				$saida = $precos["produto_id"];
				$saida .= ">";
				$saida .= $precos["descricao"];
				$saida .= ">";
				$saida .= $precos["loja_id"];
				$saida .= ">";
				$saida .= $precos["preco"];
				$saida .= ">";
				$saida .= $precos["dt_confirmacao"];
				$saida .= "\n";
					
			}
		}  elseif ($preco and $loja_atual){
			//echo "p: ".$preco;
			//echo "la: ".$loja_atual;
				
			$query =  " SELECT produto_id ";
			$query .= " FROM  precos ";
			$query .= " WHERE produto_id = $codBarras ";
			$query .= " AND loja_id = $loja_atual";
			
			$resultSet = mysql_query($query);
			$precos = mysql_fetch_array($resultSet);
		
			if ($precos["produto_id"]){
				$query =  "	UPDATE precos ";
				$query .= " SET preco=$preco, ";
				$query .= " 	dt_confirmacao=sysdate() ";
				$query .= " WHERE produto_id = $codBarras ";
				$query .= " AND loja_id = $loja_atual;";
			} else {
				$query =  "	INSERT INTO precos ";
				$query .= " VALUES( ";
				$query .= " 	$codBarras, ";
				$query .= " 	$loja_atual, ";
				$query .= " 	$preco, ";
				$query .= " 	sysdate())";
			}
			mysql_query($query);
			
		}
		
	} elseif ($listaProdutos){
		if ($loja_atual){
			$query =  "	SELECT pr.produto_id produto_id, ";
			$query .= " 	   pd.descricao descricao, ";
			$query .= " 	   pr.loja_id loja_id, ";
			$query .= " 	   pr.preco preco, ";
			$query .= " 	   pr.dt_confirmacao dt_confirmacao ";
			$query .= " FROM produtos pd, ";
			$query .= " 	 precos pr ";
			$query .= " WHERE pd.produto_id = pr.produto_id  ";
			$query .= " AND pr.produto_id in ($listaProdutos) ";
			$query .= " AND pr.loja_id = $loja_atual;";

			$resultSet = mysql_query($query);
			while($precos = mysql_fetch_array($resultSet)){
				$saida = $precos["produto_id"];
				$saida .= ">";
				$saida .= $precos["descricao"];
				$saida .= ">";
				$saida .= $precos["loja_id"];
				$saida .= ">";
				$saida .= $precos["preco"];
				$saida .= ">";
				$saida .= $precos["dt_confirmacao"];
				$saida .= "\n";
			}
		}
	}
	echo $saida;
	

?>

