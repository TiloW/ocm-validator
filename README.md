# Optimal Crossing Minimization Validator

This program aims to validate the correctness of [crossing numbers](http://en.wikipedia.org/wiki/Crossing_number_(graph_theory)).

[![Build Status](https://travis-ci.org/TiloW/ocm-validator.svg?branch=master)](https://travis-ci.org/TiloW/ocm-validator) [![Coverage Status](https://img.shields.io/coveralls/TiloW/ocm-validator.svg)](https://coveralls.io/r/TiloW/ocm-validator?branch=master)

## Purpose
Given a graph `G` and a set of [Kuratowski subdivisions](http://en.wikipedia.org/wiki/Kuratowski%27s_theorem) it can be shown that the crossing number of `G` exceeds some lower bound using linear programming. Existing algorithms are able to identify a small subset of the exponentially large set of Kuratowski subdivisions, such that the bound grows tight. However, all implementations lack the required readability to comprehend the supposed proof.

The Validator will be used to track down bugs in the existing implementations and to validate generated proofs. Sufficient readability is a major objective.

## Requirements
You need one of the following linear program solvers available on your command line:
  * `gurobi_cl` (http://www.gurobi.com)
  * `scip` (http://scip.zib.de)
  * `cplex`

Java Runtime Environment 7 or higher is required to run the program.

## References
This program is part of a master thesis developed at [Osnabrück University](http://www.uni-osnabrueck.de/en/home.html) ([Algorithm Engineering group](http://www-lehre.informatik.uni-osnabrueck.de/theoinf/index/start)).
The [**O**pen **G**raph **D**rawing **F**ramework](http://ogdf.net) is utilized for extracting the set of Kuratowski subdivions.
My work is based on [Computing Crossing Numbers](http://www.ae.uni-jena.de/alenmedia/de/dokumente/ComputingCrossingNumbers_PhDthesis_Chimani_pdf.pdf) PhD Thesis by M. Chimani, Technical University Dortmund, 2008.

## License
Copyright (c) 2015 Tilo Wiedera

This program is released unter the [MIT License](https://github.com/TiloW/ocm-validator/blob/master/LICENSE.txt).
