import React from "react";

function ButtonWrapper({ clickable, children }) {
  return (
    <div>
      {React.cloneElement(children, {
        disabled: !clickable,
        style: { opacity: clickable ? 1 : 0.5 },
      })}
    </div>
  );
}

export default ButtonWrapper;
