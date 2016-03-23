package com.gu.identity.frontend

import com.gu.identity.frontend.errors.AppException

package object services {

  type ServiceException = AppException
  type ServiceExceptions = Seq[AppException]

}
