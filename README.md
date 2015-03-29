# StoreResponsePropertyAction [![Build Status](https://travis-ci.org/testify/StoreResponsePropertyAction.svg?branch=master)](https://travis-ci.org/testify/StoreResponsePropertyAction)
*A Testify Action to store test properties from a processor response*

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/testify/testify?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# Usage
### StoreResponsePropertyAction
  *Stores information from response objects into properties for use in tests*
####Parameters
  *The following parameters are supported*
* None:             _Start, End string match (Default)_
* **|XPATH|**       _Switches to xpath based matching_
* **|NODE|**        _Selects entire node/nodelist items using xpath_

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

### Node Match

    <testcase>
      <postTestProcessorAction>
        StoreResponsePropertyAction::|NODE|PropertyName==//path/to/node
      </postTestProcessorAction>
    </testcase>
