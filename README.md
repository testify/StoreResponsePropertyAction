# StoreResponsePropertyAction [![Build Status](https://travis-ci.org/testify/StoreResponsePropertyAction.svg?branch=master)](https://travis-ci.org/testify/StoreResponsePropertyAction)
*A Testify Action to store test properties from a processor response*

# Usage
### StoreResponsePropertyAction
  *Stores information from response objects into properties for use in tests*
####Parameters
  *The following parameters are supported*
* None:             _Start, End string match (Default)_
* **|XPATH|**       _Switches to xpath based matching_

## Example
### Simple String match

    <testcase>
      <postTestProcessorAction>
        StoreResponsePropertyAction::PropertyName==StartTag==EndTag
      </postTestProcessorAction>
    </testcase>

### Xpath Match

    <testcase>
      <postTestProcessorAction>
        StoreResponsePropertyAction::|XPATH|PropertyName==//path/to/node
      </postTestProcessorAction>
    </testcase>

