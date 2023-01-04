/*
 * Copyright 2019 Alberto Paro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.auth.permission

import zio.auth.InvalidPermissionStringException

/**
 * Provides a base Permission class from which type-safe/domain-specific
 * subclasses may extend. Can be used as a base class for JPA/Hibernate
 * persisted permissions that wish to store the parts of the permission string
 * in separate columns (e.g. 'domain', 'actions' and 'targets' columns), which
 * can be used in querying strategies.
 */
final case class DomainPermission(
  domain: String,
  actions: Set[String] = Set.empty[String],
  targets: Set[String] = Set.empty[String],
  parts: List[Set[String]] = Nil
) extends WildcardPermissionTrait {

  def getDomain: String = domain

  def setDomain(domain: String): DomainPermission =
    if (domain.nonEmpty && this.domain == domain) {
      this
    } else {
      DomainPermission(domain, actions, targets)
    }

  def getActions: Set[String] = actions

  def setActions(actions: Set[String]): DomainPermission =
    if (this.actions == actions) {
      this
    } else {
      DomainPermission(domain, actions, targets)
    }

  def getTargets: Set[String] = targets

  def setTargets(targets: Set[String]): DomainPermission =
    if (this.targets == targets) {
      this
    } else {
      DomainPermission(domain, actions, targets)
    }
}

object DomainPermission {

  def apply(): DomainPermission = DomainPermission("")

  def apply(actions: String): DomainPermission = {
    val domain = getDomain(classOf[DomainPermission])
    new DomainPermission(
      domain,
      actions =
        if (actions.isEmpty) Set.empty[String]
        else actions.split(WildcardPermission.SUBPART_DIVIDER_TOKEN).toSet,
      parts = encodeParts(domain, actions = actions)
    )
  }

  def apply(actions: String, targets: String): DomainPermission = {
    val domain = getDomain(classOf[DomainPermission])
    new DomainPermission(
      domain,
      actions =
        if (actions.isEmpty) Set.empty[String]
        else actions.split(WildcardPermission.SUBPART_DIVIDER_TOKEN).toSet,
      targets =
        if (targets.isEmpty) Set.empty[String]
        else targets.split(WildcardPermission.SUBPART_DIVIDER_TOKEN).toSet,
      parts = encodeParts(domain, actions = actions, targets = targets)
    )
  }

  def apply(actions: Set[String], targets: Set[String]): DomainPermission = {
    val domain = getDomain(classOf[DomainPermission])
    new DomainPermission(
      domain,
      actions = actions,
      targets = targets,
      parts = encodeParts(
        domain,
        actions = actions.mkString(WildcardPermission.SUBPART_DIVIDER_TOKEN),
        targets = targets.mkString(WildcardPermission.SUBPART_DIVIDER_TOKEN)
      )
    )
  }

  def apply(
    domain: String,
    actions: Set[String],
    targets: Set[String]
  ): DomainPermission =
    new DomainPermission(
      domain,
      actions = actions,
      targets = targets,
      parts = encodeParts(
        domain,
        actions = actions.mkString(WildcardPermission.SUBPART_DIVIDER_TOKEN),
        targets = targets.mkString(WildcardPermission.SUBPART_DIVIDER_TOKEN)
      )
    )

  def getDomain(clazz: Class[_ <: DomainPermission]): String = {
    var domain: String = clazz.getSimpleName.toLowerCase
    val index: Int = domain.lastIndexOf("permission")
    if (index != -1) {
      domain = domain.substring(0, index)
    }
    domain
  }

  private def encodeParts(
    domain: String,
    actions: String,
    targets: String = ""
  ): List[Set[String]] = {
    if (domain.isEmpty) {
      throw InvalidPermissionStringException(
        domain,
        "domain argument cannot be null or empty."
      )
    }
    val sb: StringBuilder = new StringBuilder(domain)
    if (actions.isEmpty) {
      if (targets.nonEmpty) {
        sb.append(WildcardPermission.PART_DIVIDER_TOKEN).append(WildcardPermission.WILDCARD_TOKEN)
      }
    } else {
      sb.append(WildcardPermission.PART_DIVIDER_TOKEN).append(actions)
    }
    if (targets.nonEmpty) {
      sb.append(WildcardPermission.PART_DIVIDER_TOKEN).append(targets)
    }
    WildcardPermission.computeParts(sb.toString())
  }

}
